package org.mayocat.shop.catalog.web.object

import org.mayocat.configuration.ConfigurationService
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.shop.catalog.model.Product
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory

/**
 * Web object that represents a list of products. See {@link CollectionWebObject} for example.
 *
 * @version $Id$
 */
class ProductListWebObject
{
    List<ProductWebObject> list

    PaginationWebObject pagination
}
