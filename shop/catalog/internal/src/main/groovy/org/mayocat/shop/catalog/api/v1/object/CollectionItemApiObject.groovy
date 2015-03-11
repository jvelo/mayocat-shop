/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
     * See {@link CollectionApi#updateCollectionTree()}
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
