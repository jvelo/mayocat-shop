/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.resources;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.LocaleUtils;
import org.mayocat.rest.Resource;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

/**
 * @version $Id$
 */
@Component(LocalesResource.PATH)
@Path(LocalesResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
public class LocalesResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "locales";

    private Set<LocaleRepresentation> localesRepresentations;

    static class LocaleRepresentation
    {
        private String tag;

        private String name;

        public LocaleRepresentation(String tag, String name)
        {
            this.tag = tag;
            this.name = name;
        }

        public String getTag()
        {
            return tag;
        }

        public String getName()
        {
            return name;
        }
    }

    @GET
    public Response getLocales()
    {
        if (localesRepresentations == null) {
            Set<LocaleRepresentation> locales = Sets.newHashSet();
            List<Locale> availableLocales = LocaleUtils.availableLocaleList();
            for (final Locale locale : availableLocales) {
                StringBuilder nameBuilder = new StringBuilder();
                nameBuilder.append(locale.getDisplayLanguage());
                if (!Strings.isNullOrEmpty(locale.getDisplayCountry())) {
                    nameBuilder.append(" (");
                    nameBuilder.append(locale.getDisplayCountry());
                    nameBuilder.append(")");
                }
                final String name = nameBuilder.toString();
                locales.add(new LocaleRepresentation(locale.toLanguageTag(), name));
            }
            Ordering<LocaleRepresentation> nameOrdering =
                    Ordering.natural().onResultOf(new Function<LocaleRepresentation, String>()
                    {
                        public String apply(LocaleRepresentation from)
                        {
                            return from.getName();
                        }
                    });

            localesRepresentations = ImmutableSortedSet.orderedBy(nameOrdering).addAll(locales).build();
        }

        return Response.ok(localesRepresentations).build();
    }
}
