/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.CompileStatic

/**
 * Attributes that are common to all API objects
 *
 * @version $Id$
 */
@CompileStatic
class BaseApiObject
{
    @JsonIgnore
    Map<String, LinkApiObject> _links

    // Shortcut to ._links.self.href
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String _href

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("_links")
    Map<String, LinkApiObject> getLinks()
    {
        if (this._links != null) {
            return this._links;
        } else if (this._href != null) {
            this._links = [
                    self: new LinkApiObject([href: _href])
            ]
        }
    }
}
