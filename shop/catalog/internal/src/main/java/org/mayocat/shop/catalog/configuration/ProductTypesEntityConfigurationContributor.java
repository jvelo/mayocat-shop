/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.configuration;

import java.util.Map;

import javax.inject.Inject;

import org.mayocat.configuration.gestalt.EntityConfigurationContributor;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.theme.TypeDefinition;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("productTypes")
public class ProductTypesEntityConfigurationContributor implements EntityConfigurationContributor
{
    @Inject
    private CatalogSettings catalogSettings;

    @Override
    public String contributesTo()
    {
        return "product";
    }

    @Override
    public void contribute(Map<String, Object> configuration)
    {
        if (!configuration.containsKey("types")) {
            configuration.put("types", Maps.newHashMap());
        }
        ((Map<String, TypeDefinition>) configuration.get("types"))
                .putAll(catalogSettings.getProductsSettings().getTypes());
    }
}
