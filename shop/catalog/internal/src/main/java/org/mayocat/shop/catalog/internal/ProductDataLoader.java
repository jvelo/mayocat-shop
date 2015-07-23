/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.internal;

import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import org.mayocat.entity.DataLoaderAssistant;
import org.mayocat.entity.EntityData;
import org.mayocat.entity.LoadingOption;
import org.mayocat.model.Entity;
import org.mayocat.shop.catalog.ProductLoadingOptions;
import org.mayocat.shop.catalog.model.Feature;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("products")
public class ProductDataLoader implements DataLoaderAssistant
{
    @Inject
    private ProductStore productStore;

    @Inject
    private Logger logger;

    @Override
    public <E extends Entity> void load(EntityData<E> entity, LoadingOption... options) {
        if (!Product.class.isAssignableFrom(entity.getEntity().getClass())) {
            return;
        }

        List<LoadingOption> optionsAsList = Arrays.asList(options);
        if (optionsAsList.contains(ProductLoadingOptions.WITH_FEATURE_AND_VARIANTS)) {
            entity.setChildren(Feature.class, this.productStore.findFeatures((Product) entity.getEntity()));
            entity.setChildren(Product.class, this.productStore.findVariants((Product) entity.getEntity()));
        }
    }

    @Override
    public <E extends Entity> void loadList(List<EntityData<E>> entities, LoadingOption... options) {
        // TODO improve perf by doing only one query
        for (EntityData<E> entityData : entities) {
            load(entityData, options);
        }
    }

    @Override
    public Integer priority() {
        return 500;
    }
}
