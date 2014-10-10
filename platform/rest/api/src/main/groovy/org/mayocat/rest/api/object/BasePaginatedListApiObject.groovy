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
import groovy.text.SimpleTemplateEngine
import groovy.text.Template
import groovy.text.TemplateEngine
import groovy.transform.CompileStatic

/**
 * Base class for paginated lists api objects : handles the creation of the proper links to pages
 *
 * @version $Id$
 */
@CompileStatic
class BasePaginatedListApiObject
{
    @JsonIgnore
    static TemplateEngine templateEngine = new SimpleTemplateEngine()

    @JsonIgnore
    static Map<String, Template> templateCache = [:]

    @JsonIgnore
    static Template getTemplate(String template)
    {
        if (!templateCache.containsKey(template)) {
            templateCache.put(template, templateEngine.createTemplate(template as String))
        }
        templateCache.get(template)
    }

    Pagination _pagination;

    @JsonIgnore
    Map<String, LinkApiObject> _links

    // Shortcut to ._links.self.href
    @JsonIgnore
    String _href

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("_href")
    String getHref()
    {
        getLinks()
        _href
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("_links")
    Map<String, LinkApiObject> getLinks()
    {
        if (_links == null) {

            _links = [:]

            if (_pagination) {
                if (!_href) {
                    Map<String, String> arguments = [
                            offset       : _pagination.offset as String,
                            numberOfItems: _pagination.numberOfItems as String
                    ]
                    arguments.putAll(_pagination.urlArguments)
                    _href = getTemplate(_pagination.urlTemplate).make(arguments).toString()
                }

                if (_pagination.offset + _pagination.numberOfItems < _pagination.totalItems) {
                    // there is a next page
                    Map<String, String> arguments = [
                            offset       : _pagination.offset + _pagination.numberOfItems as String,
                            numberOfItems: _pagination.numberOfItems as String
                    ]
                    arguments.putAll(_pagination.urlArguments)
                    _links.nextPage = new LinkApiObject([
                            href: getTemplate(_pagination.urlTemplate).make(arguments).toString()
                    ])
                }

                if (_pagination.offset > 0) {
                    // there is a next page
                    Map<String, String> arguments = [
                            offset       : (_pagination.offset - _pagination.numberOfItems) as String,
                            numberOfItems: _pagination.numberOfItems as String
                    ]
                    arguments.putAll(_pagination.urlArguments)
                    _links.previousPage = new LinkApiObject([
                            href: getTemplate(_pagination.urlTemplate).make(arguments).toString()
                    ])
                }
            }

            if (_href != null) {
                _links.self = new LinkApiObject([href: _href])
            }
        }

        _links
    }
}
