package org.mayocat.context;

import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.application.AbstractService;
import org.mayocat.authorization.Authenticator;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.event.EventListener;
import org.mayocat.multitenancy.TenantResolver;
import org.mayocat.theme.ThemeManager;
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
    private ThemeManager themeManager;

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

        if (tenant != null) {
            Map<Class, Object> configurations = configurationService.getSettings();
            context.setSettings(configurations);
        }

        // 3. User

        Optional<User> user = Optional.absent();
        for (String headerName : Lists.newArrayList("Authorization", "Cookie")) {
            final String headerValue =
                    Strings.nullToEmpty(this.getHeaderValue(servletRequestEvent, headerName));
            for (Authenticator authenticator : this.authenticators.values()) {
                if (authenticator.respondTo(headerName, headerValue)) {
                    user = authenticator.verify(headerValue, tenant);
                }
            }
        }

        context.setUser(user.orNull());

        // 4. ThemeDefinition
        if (tenant != null) {
            context.setTheme(themeManager.getTheme());
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
        for (String staticPath : AbstractService.getStaticPaths()) {
            if (path.startsWith(staticPath)) {
                return true;
            }
        }
        return false;
    }
}
