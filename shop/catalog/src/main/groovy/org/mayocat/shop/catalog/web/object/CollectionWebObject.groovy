package org.mayocat.shop.catalog.web.object

import org.mayocat.shop.front.util.ContextUtils
import org.mayocat.url.EntityURLFactory

/**
 * Web view for a {@link org.mayocat.shop.catalog.model.Collection}
 *
 * @version $Id$
 */
class CollectionWebObject
{
    String title;

    String description;

    String url;

    String slug;

    def withCollection(org.mayocat.shop.catalog.model.Collection collection, EntityURLFactory urlFactory)
    {
        title = ContextUtils.safeString(collection.title)
        description = ContextUtils.safeHtml(collection.description)
        url = urlFactory.create(collection).path
        slug = collection.slug
    }
}
