package org.mayocat.shop.context;

import java.lang.reflect.Type;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.mayocat.shop.application.MayocatShopService;
import org.mayocat.shop.authorization.Authenticator;
import org.mayocat.shop.base.EventListener;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.configuration.ConfigurationService;
import org.mayocat.shop.multitenancy.TenantResolver;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.InjectParam;

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
    private Execution execution;

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

        String host = getHost(servletRequestEvent);
        Tenant tenant = this.tenantResolver.get().resolve(host);

        Context context = new Context(tenant, null);

        // Set the context in the execution already, even if we haven't figured out if there is a valid user yet.
        // The context tenant is actually needed to find out the context user and to initialize tenant configurations
        this.execution.setContext(context);

        Map<Class, Object> configurations = configurationService.getConfigurations();
        context.setConfigurations(configurations);

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
        for (String staticPath : MayocatShopService.STATIC_PATHS) {
            if (path.startsWith(staticPath)) {
                return true;
            }
        }
        return false;
    }
}
