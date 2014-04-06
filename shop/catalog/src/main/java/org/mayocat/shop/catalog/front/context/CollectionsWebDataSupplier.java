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
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.context.WebContext;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.shop.front.WebDataSupplier;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;

/**
 * Supplies collections data to all web views : offer the list of collections
 *
 * @version $Id$
 */
@Component("collections")
public class CollectionsWebDataSupplier implements WebDataSupplier, ContextConstants
{
    @Inject
    private Provider<CollectionStore> collectionStore;

    @Inject
    private EntityURLFactory urlFactory;

    @Inject
    private WebContext webContext;

    @Inject
    private EntityLocalizationService entityLocalizationService;

    @Override
    public void supply(Map<String, Object> data)
    {
        List<Collection> collections = this.collectionStore.get().findAll();
        final List<Map<String, Object>> collectionsContext = Lists.newArrayList();

        for (final Collection collection : collections) {
            final Collection localized = entityLocalizationService.localize(collection);
            final String collectionPath = urlFactory.create(localized).getPath();
            // Determine if this is the currently browsed collection by compoaring its path to the current request path.
            // We don't use the canonical path for the comparison since the URL we got for the collection is localized.
            final boolean current = collectionPath.equals(webContext.getRequest().getPath());
            collectionsContext.add(new HashMap<String, Object>()
            {
                {
                    put(ContextConstants.URL, collectionPath);
                    put("slug", localized.getSlug());
                    put("title", localized.getTitle());
                    put("description", localized.getDescription());
                    put("current", current);
                    //  TODO: featured image
                }
            });
        }

        data.put(COLLECTIONS, collectionsContext);
    }
}
