/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic
import org.mayocat.rest.web.object.EntityModelWebObject
import org.mayocat.shop.front.util.ContextUtils
import org.mayocat.url.EntityURLFactory

/**
 * @version $Id$
 */
@CompileStatic
class AbstractCollectionWebObject
{
    String title

    String description

    String url

    String slug

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityModelWebObject model

    String template

    @Deprecated
    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> theme_addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map <String, Object> addons

    AbstractCollectionWebObject withCollection(org.mayocat.shop.catalog.model.Collection collection, EntityURLFactory urlFactory)
    {
        title = ContextUtils.safeString(collection.title)
        description = ContextUtils.safeHtml(collection.description)
        url = urlFactory.create(collection).path
        slug = collection.slug

        this
    }

    AbstractCollectionWebObject withAddons(Map<String, Object> addons) {
        theme_addons = addons
        this.addons = addons

        this
    }

}
