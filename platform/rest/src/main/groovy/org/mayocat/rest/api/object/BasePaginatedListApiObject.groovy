package org.mayocat.rest.api.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.text.SimpleTemplateEngine
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
    Pagination pagination;

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

            if (pagination) {
                def templateEngine = new SimpleTemplateEngine()

                if (!_href) {
                    Map<String, String> arguments = [
                            offset: pagination.offset as String,
                            numberOfItems: pagination.numberOfItems as String
                    ]
                    arguments.putAll(pagination.urlArguments)
                    _href = templateEngine.createTemplate(pagination.urlTemplate as String).make(arguments).toString()
                }

                if (pagination.offset + pagination.numberOfItems < pagination.totalItems) {
                    // there is a next page
                    Map<String, String> arguments = [
                            offset: pagination.offset + pagination.numberOfItems as String,
                            numberOfItems: pagination.numberOfItems as String
                    ]
                    arguments.putAll(pagination.urlArguments)
                    _links.nextPage = new LinkApiObject([
                        href: templateEngine.createTemplate(pagination.urlTemplate as String).make(arguments).toString()
                    ])
                }

                if (pagination.offset > 0) {
                    // there is a next page
                    Map<String, String> arguments = [
                            offset: (pagination.offset - pagination.numberOfItems) as String,
                            numberOfItems: pagination.numberOfItems as String
                    ]
                    arguments.putAll(pagination.urlArguments)
                    _links.previousPage = new LinkApiObject([
                            href: templateEngine.createTemplate(pagination.urlTemplate as String).make(arguments).toString()
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
