package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.rest.api.object.BaseApiObject

/**
 * @version $Id$
 */
@CompileStatic
class CollectionItemApiObject extends BaseApiObject
{
    String slug

    String title

    List<CollectionItemApiObject> children = []

    CollectionItemApiObject withCollection(org.mayocat.shop.catalog.model.Collection collection)
    {
        slug = collection.slug
        title = collection.title

        this
    }
}
