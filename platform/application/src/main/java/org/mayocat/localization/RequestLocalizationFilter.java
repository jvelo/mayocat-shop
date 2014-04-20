/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.localization;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mayocat.application.AbstractService;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.LocalizationFilterSettings;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.configuration.general.LocalesSettings;
import org.mayocat.context.WebContext;
import org.mayocat.servlet.ServletFilter;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;

/**
 * Servlet filter for web requests that checks if the language cookie has been set. If it has not been set, tries to
 * find the best locale we can serve according to user agent and available locales for the web site.
 *
 * Future possible improvement: iterate over each locale in ServletRequest#getLocales instead of the first one from
 * ServletRequest#getLocale
 *
 * FIXME: this should be in the localization module but there's a dependency on AbstractService that would introduce
 * a cyclic dependency chain. The proper way to resolve this would be to introduce a component that can resolves if a
 * certain path is an static path, or API path or Web path...
 *
 * @version $Id$
 */
@Component("requestLocalizationFilter")
public class RequestLocalizationFilter implements Filter, ServletFilter
{
    @Inject
    private ConfigurationService configurationService;

    @Inject
    private LocalizationFilterSettings settings;

    @Inject
    private WebContext webContext;

    public void init(FilterConfig filterConfig) throws ServletException
    {
        // Nothing
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException
    {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (isStaticPath(request.getRequestURI())
                || FluentIterable.from(settings.getExcludePaths()).anyMatch(startsWithPath(request))
                || webContext.getTenant() == null)
        {
            // Ignore static paths or paths configured as excluded
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        Optional<Cookie> languageCookie =
                FluentIterable.from(Arrays.asList(request.getCookies())).filter(new Predicate<Cookie>()
                {
                    public boolean apply(Cookie cookie)
                    {
                        return cookie.getName().equals("language");
                    }
                }).first();

        if (languageCookie.isPresent()) {
            // Cookie already set, there's no work for us
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        if (!languageCookie.isPresent()) {

            LocalesSettings localesSettings = configurationService.getSettings(GeneralSettings.class).getLocales();
            List<Locale> alternativeLocales =
                    FluentIterable.from(localesSettings.getOtherLocales().getValue())
                            .filter(Predicates.notNull()).toList();

            // Maybe there is a better locale matching the user Accept-Language
            String acceptLanguage = request.getHeader("Accept-Language");
            if (!Strings.isNullOrEmpty(acceptLanguage)) {
                final Locale acceptLanguageAsLocale = request.getLocale();
                Optional<Locale> bestMatch = FluentIterable.from(alternativeLocales).filter(new Predicate<Locale>()
                {
                    public boolean apply(Locale input)
                    {
                        return input.equals(acceptLanguageAsLocale);
                    }
                }).first();

                if (!bestMatch.isPresent() &&
                        !Strings.isNullOrEmpty(acceptLanguageAsLocale.getDisplayCountry()))
                {
                    final Locale onlyLanguageAcceptLanguage = new Locale(acceptLanguageAsLocale.getLanguage());
                    bestMatch = FluentIterable.from(alternativeLocales).filter(new Predicate<Locale>()
                    {
                        public boolean apply(Locale input)
                        {
                            return input.equals(onlyLanguageAcceptLanguage);
                        }
                    }).first();
                }

                if (bestMatch.isPresent()) {
                    // Mark the language as set with a cookie
                    Cookie cookie = new Cookie("language", "set");
                    cookie.setMaxAge(-1);
                    cookie.setPath("/");
                    response.addCookie(cookie);

                    // Redirect to the found language
                    response.sendRedirect("/" + bestMatch.get().toLanguageTag() + request.getRequestURI());
                }
            }
        }

        chain.doFilter(servletRequest, servletResponse);
    }

    public void destroy()
    {
        // Nothing
    }

    public String urlPattern()
    {
        return "*";
    }

    private static final Predicate<String> startsWithPath(final HttpServletRequest request)
    {
        return new Predicate<String>()
        {
            public boolean apply(String input)
            {
                return request.getRequestURI().startsWith(input);
            }
        };
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
