package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.rest.api.object.BaseApiObject

/**
 * @version $Id$
 */
@CompileStatic
class CollectionItemApiObject extends BaseApiObject
{
    /**
     * Usually we don't expose internal IDs in API, but here it is needed so that we can differentiate items that have
     * the same slug but different parents.
     *
     * See {@link org.mayocat.shop.catalog.api.v1.CollectionApi#updateCollectionTree()}
     */
    String _id

    String slug

    String title

    List<String> parentSlugs

    List<CollectionItemApiObject> children = []

    CollectionItemApiObject withCollection(org.mayocat.shop.catalog.model.Collection collection)
    {
        slug = collection.slug
        title = collection.title
        _id = collection.id

        this
    }
}
