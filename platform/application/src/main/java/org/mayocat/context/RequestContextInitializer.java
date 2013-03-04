package org.mayocat.context;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.mayocat.application.AbstractService;
import org.mayocat.authorization.Authenticator;
import org.mayocat.base.EventListener;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.theme.ThemeConfiguration;
import org.mayocat.multitenancy.TenantResolver;
import org.mayocat.theme.ThemeLoader;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
@Component
@Named("requestContextInitializer")
public class RequestContextInitializer implements ServletRequestListener, EventListener
{
    @Inject
    protected Provider<TenantResolver> tenantResolver;

    @Inject
    private Map<String, Authenticator> authenticators;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private ThemeLoader themeLoader;

    @Inject
    private Execution execution;

    @Inject
    private Logger logger;

    public void requestDestroyed(ServletRequestEvent servletRequestEvent)
    {
        if (isStaticPath(((HttpServletRequest) servletRequestEvent.getServletRequest()).getRequestURI())) {
            return;
        }
        this.execution.setContext(null);
    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent)
    {
        if (isStaticPath(this.getRequestURI(servletRequestEvent))) {
            return;
        }

        // 1. Tenant

        String host = getHost(servletRequestEvent);
        Tenant tenant = this.tenantResolver.get().resolve(host);

        Context context = new Context(tenant, null);

        // Set the context in the execution already, even if we haven't figured out if there is a valid user yet.
        // The context tenant is actually needed to find out the context user and to initialize tenant configurations
        this.execution.setContext(context);

        // 2. Configurations

        Map<Class, Object> configurations = configurationService.getConfigurations();
        context.setConfigurations(configurations);

        // 3. User

        Optional<User> user = Optional.absent();
        if (tenant != null) {
            // Right now we only support tenant-linked user accounts.
            // In the future we will introduce "global" (or "marketplace") accounts
            for (String headerName : Lists.newArrayList("Authorization", "Cookie")) {
                final String headerValue = Strings.nullToEmpty(this.getHeaderValue(servletRequestEvent, headerName));
                for (Authenticator authenticator : this.authenticators.values()) {
                    if (authenticator.respondTo(headerName, headerValue)) {
                        user = authenticator.verify(headerValue, tenant);
                    }
                }
            }
        }
        context.setUser(user.orNull());

        // 4. Theme
        ThemeConfiguration configuration = (ThemeConfiguration)
                this.configurationService.getConfiguration(ThemeConfiguration.class);
        String activeTheme = configuration.getActive().getValue();
        try {
            context.setTheme(themeLoader.load(activeTheme));
        } catch (IOException e) {
            logger.warn("Failed to load theme with name [{}]", activeTheme);
            String defaultTheme = configuration.getActive().getDefaultValue();
            try {
                context.setTheme(themeLoader.load(defaultTheme));
            } catch (IOException e1) {
                logger.error("Failed to load default theme with name [{}]", defaultTheme);
                throw new RuntimeException(e1);
            }
        }
    }

    private String getHeaderValue(ServletRequestEvent event, String headerName)
    {
        return ((HttpServletRequest) event.getServletRequest()).getHeader(headerName);
    }


    private String getHost(ServletRequestEvent event)
    {
        return ((HttpServletRequest) event.getServletRequest()).getServerName();
    }

    private String getRequestURI(ServletRequestEvent event)
    {
        return ((HttpServletRequest) event.getServletRequest()).getRequestURI();
    }


    private boolean isStaticPath(String path) {
        for (String staticPath : AbstractService.STATIC_PATHS) {
            if (path.startsWith(staticPath)) {
                return true;
            }
        }
        return false;
    }
}
