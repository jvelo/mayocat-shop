/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.image.model.Image
import org.mayocat.rest.web.object.EntityImagesWebObject
import org.mayocat.rest.web.object.EntityModelWebObject
import org.mayocat.rest.web.object.ImageWebObject
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Feature
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.front.util.ContextUtils
import org.mayocat.shop.taxes.configuration.TaxesSettings
import org.mayocat.theme.FeatureDefinition
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.theme.TypeDefinition
import org.mayocat.url.EntityURLFactory

/**
 * Web object for a {@link Product} representation
 *
 * @version $Id$
 */
@CompileStatic
class ProductWebObject extends AbstractProductWebObject implements WithTenantImages
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    CollectionWebObject featuredCollection

    /**
     * @deprecated use #featuredCollection instead
     */
    @Deprecated
    @JsonInclude(JsonInclude.Include.NON_NULL)
    CollectionWebObject featured_collection

    def withCollection(org.mayocat.shop.catalog.model.Collection collection, EntityURLFactory urlFactory)
    {
        featuredCollection = new CollectionWebObject()
        featuredCollection.withCollection(collection, urlFactory)

        // For backward compatibility
        featured_collection = featuredCollection
    }
}
