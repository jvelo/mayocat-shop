/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context;

import java.io.Serializable;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.model.User;
import org.mayocat.application.AbstractService;
import org.mayocat.authorization.Authenticator;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.configuration.general.LocalesSettings;
import org.mayocat.context.internal.DefaultWebContext;
import org.mayocat.context.internal.ThreadLocalWebContext;
import org.mayocat.context.internal.request.DefaultWebRequest;
import org.mayocat.event.EventListener;
import org.mayocat.multitenancy.TenantResolver;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.UserAgentBreakpointDetector;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.CharMatcher;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
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
    private UserAgentBreakpointDetector breakpointDetector;

    @Inject
    @Named("default") // -> This is the ThreadLocalWebContext
    private WebContext context;

    @Inject
    private Logger logger;

    private static final Predicate<String> IS_BLANK = matchesAllOf(CharMatcher.WHITESPACE);

    private static final Predicate<String> IS_NULL_OR_BLANK = Predicates.and(Predicates.notNull(), IS_BLANK);

    private static Predicate<String> matchesAllOf(final CharMatcher charMatcher)
    {
        return new Predicate<String>()
        {
            public boolean apply(String string)
            {
                return charMatcher.matchesAllOf(string);
            }
        };
    }

    public void requestDestroyed(ServletRequestEvent servletRequestEvent)
    {
        if (isStaticPath(((HttpServletRequest) servletRequestEvent.getServletRequest()).getRequestURI())) {
            return;
        }

        ((ThreadLocalWebContext) this.context).setContext(null);
    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent)
    {
        if (isStaticPath(this.getRequestURI(servletRequestEvent))) {
            return;
        }

        // 1. Tenant

        String host = getHost(servletRequestEvent);
        Tenant tenant = this.tenantResolver.get().resolve(host);

        DefaultWebContext context = new DefaultWebContext(tenant, null);

        // Set the context in the context already, even if we haven't figured out if there is a valid user yet.
        // The context tenant is actually needed to find out the context user and to initialize tenant configurations
        ((ThreadLocalWebContext) this.context).setContext(context);

        // 2. Configurations

        if (tenant != null) {
            Map<Class, Serializable> configurations = configurationService.getSettings();
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

        if (tenant != null) {
            // 4. ThemeDefinition
            context.setTheme(themeManager.getTheme());

            // 5. Locale
            LocalesSettings localesSettings = configurationService.getSettings(GeneralSettings.class).getLocales();
            boolean localeSet = false;
            List<Locale> alternativeLocales =
                    FluentIterable.from(localesSettings.getOtherLocales().getValue())
                            .filter(Predicates.notNull()).toList();

            String path = getPath(servletRequestEvent);
            String canonicalPath = path;
            if (!alternativeLocales.isEmpty()) {
                for (Locale locale : alternativeLocales) {
                    List<String> fragments = ImmutableList
                            .copyOf(Collections2
                                    .filter(Arrays.asList(path.split("/")), Predicates.not(IS_NULL_OR_BLANK)));
                    if (fragments.size() > 0 && fragments.get(0).equals(locale.toLanguageTag())) {
                        context.setLocale(locale);
                        context.setAlternativeLocale(true);
                        canonicalPath = StringUtils.substringAfter(canonicalPath, "/" + locale);
                        localeSet = true;
                        break;
                    }
                }
            }
            if (!localeSet) {
                context.setLocale(localesSettings.getMainLocale().getValue());
                context.setAlternativeLocale(false);
            }

            if (context.isAlternativeLocale()) {
                path = StringUtils.substringAfter(path, context.getLocale().toLanguageTag());
            }

            // 6. Request
            Optional<Breakpoint> breakpoint = this.breakpointDetector.getBreakpoint(getUserAgent(servletRequestEvent));
            context.setRequest(new DefaultWebRequest(getBaseURI(servletRequestEvent), canonicalPath, path, breakpoint));
        }
    }

    private URI getBaseURI(ServletRequestEvent event)
    {
        HttpServletRequest request = (HttpServletRequest) event.getServletRequest();
        if ((request.getServerPort() == 80) ||
                (request.getServerPort() == 443))
        {
            return URI.create(request.getScheme() + "://" +
                    request.getServerName() +
                    request.getContextPath() + '/');
        } else {
            return URI.create(request.getScheme() + "://" +
                    request.getServerName() + ":" + request.getServerPort() +
                    request.getContextPath() + '/');
        }
    }

    private String getPath(ServletRequestEvent event)
    {
        return ((HttpServletRequest) event.getServletRequest()).getPathInfo();
    }

    private String getHeaderValue(ServletRequestEvent event, String headerName)
    {
        return ((HttpServletRequest) event.getServletRequest()).getHeader(headerName);
    }

    private String getHost(ServletRequestEvent event)
    {
        return event.getServletRequest().getServerName();
    }

    private String getRequestURI(ServletRequestEvent event)
    {
        return ((HttpServletRequest) event.getServletRequest()).getRequestURI();
    }

    private String getUserAgent(ServletRequestEvent event)
    {
        return ((HttpServletRequest) event.getServletRequest()).getHeader("User-Agent");
    }

    private boolean isStaticPath(String path)
    {
        for (String staticPath : AbstractService.getStaticPaths()) {
            if (path.startsWith(staticPath)) {
                return true;
            }
        }
        return false;
    }
}
