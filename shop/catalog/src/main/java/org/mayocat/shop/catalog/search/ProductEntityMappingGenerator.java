/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.search;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.mayocat.search.elasticsearch.AbstractGenericEntityMappingGenerator;
import org.mayocat.search.elasticsearch.EntityMappingGenerator;
import org.mayocat.shop.catalog.model.Product;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("product")
public class ProductEntityMappingGenerator extends AbstractGenericEntityMappingGenerator
{
    @Inject
    @Named("tenant")
    private EntityMappingGenerator tenantGenerator;

    @Override
    public Class forClass()
    {
        return Product.class;
    }

    @Override
    public Map<String, Object> generateMapping()
    {
        Map<String, Object> generated = super.generateMapping();

        Map<String, Object> site = tenantGenerator.generateMapping();

        ((Map<String, Object>) ((Map<String, Object>) generated.get("product")).get("properties"))
                .put("site", site.get("tenant"));

        return generated;
    }
}
