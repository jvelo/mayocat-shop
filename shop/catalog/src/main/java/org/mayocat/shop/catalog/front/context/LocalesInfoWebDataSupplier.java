/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.front.context;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.shop.front.WebDataSupplier;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * Data supplier that adds locale-related information in the data map (current locale, available locales, the locale
 * path, the url,
 *
 * @version $Id$
 */
@Component("localesInfo")
public class LocalesInfoWebDataSupplier implements WebDataSupplier
{
    @Inject
    private WebContext context;

    @Override
    public void supply(Map<String, Object> data)
    {
        // Manage list of locales and corresponding links
        String path = context.getRequest().getCanonicalPath();
        Map<String, Map<String, String>> locales = Maps.newHashMap();
        GeneralSettings settings = context.getSettings(GeneralSettings.class);
        final Locale mainLocale = settings.getLocales().getMainLocale().getValue();
        locales.put(mainLocale.toLanguageTag(), buildLocale(mainLocale, path, true));
        List<Locale> alternativeLocales =
                FluentIterable.from(settings.getLocales().getOtherLocales().getValue()).filter(Predicates.notNull())
                        .toList();
        if (!alternativeLocales.isEmpty()) {
            for (final Locale locale : alternativeLocales) {
                locales.put(locale.toLanguageTag(), buildLocale(locale, path, false));
            }
        }

        data.put("locales", locales);
        data.put("locale", buildLocale(context.getLocale(), path, mainLocale.equals(context.getLocale())));
        data.put("localePath", context.isAlternativeLocale() ? ("/" + context.getLocale().toLanguageTag()) : "");
        data.put("url", data.get("localePath") + path);
        data.put("canonicalUrl", path);
    }

    private Map<String, String> buildLocale(final Locale locale, final String path, final boolean isMainLocale)
    {
        return new HashMap()
        {
            {
                put("url", (isMainLocale ? "" : "/" + locale.toLanguageTag()) + path);
                put("tag", locale.toLanguageTag());
                put("code", locale.getLanguage());
                put("country", locale.getDisplayCountry(locale));
                put("language", locale.getDisplayLanguage(locale));
                put("current", locale.equals(context.getLocale()));
            }
        };
    }
}
