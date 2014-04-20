/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.search;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.search.EntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.AbstractGenericEntityIndexDocumentPurveyor;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component
public class CollectionIndexDocumentPurveyor extends AbstractGenericEntityIndexDocumentPurveyor<Collection>
        implements EntityIndexDocumentPurveyor<Collection>
{
    @Inject
    private Logger logger;

    @Inject
    private CollectionStore collectionStore;

    public Class forClass()
    {
        return Product.class;
    }

    public Map<String, Object> purveyDocument(Collection entity, Tenant tenant)
    {
        Map<String, Object> source = Maps.newHashMap();

        source.put("site", extractSourceFromEntity(tenant, tenant));
        source.putAll(extractSourceFromEntity(entity, tenant));

        return source;
    }
}
