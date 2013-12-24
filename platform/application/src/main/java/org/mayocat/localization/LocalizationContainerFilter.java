/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.localization;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.UriBuilder;

import org.mayocat.application.AbstractService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.util.Utils;

import com.google.common.base.CharMatcher;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;

/**
 * @version $Id$
 */
public class LocalizationContainerFilter implements ContainerRequestFilter
{
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

    @Override
    public ContainerRequest filter(ContainerRequest containerRequest)
    {
        if (isStaticPath(containerRequest.getRequestUri().getPath())) {
            return containerRequest;
        }

        WebContext context = Utils.getComponent(WebContext.class);

        if (context.getTenant() == null) {
            return containerRequest;
        }

        boolean localeSet = false;
        GeneralSettings settings = context.getSettings(GeneralSettings.class);
        URI requestURI = containerRequest.getRequestUri();

        List<Locale> alternativeLocales =
                FluentIterable.from(settings.getLocales().getOtherLocales().getValue())
                        .filter(Predicates.notNull()).toList();

        if (!alternativeLocales.isEmpty()) {
            for (Locale locale : alternativeLocales) {
                List<String> fragments = ImmutableList.copyOf(
                        Collections2.filter(Arrays.asList(requestURI.getPath().toString().split("/")),
                                Predicates.not(IS_NULL_OR_BLANK))
                );
                if (fragments.size() > 0 && fragments.get(0).equals(locale.toLanguageTag())) {
                    UriBuilder builder = UriBuilder.fromUri(requestURI);
                    builder.replacePath(requestURI.getPath().substring(locale.toString().length() + 1));
                    containerRequest.setUris(containerRequest.getBaseUri(), builder.build());

                    context.setLocale(locale);
                    context.setAlternativeLocale(true);
                    localeSet = true;
                    break;
                }
            }
        }

        if (!localeSet) {
            context.setLocale(settings.getLocales().getMainLocale().getValue());
            context.setAlternativeLocale(false);
        }

        return containerRequest;
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
