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
