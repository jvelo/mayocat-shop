package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic

/**
 * Web object representing a pagination ; offers access to specific pages, previous and next pages, etc.
 *
 * @version $Id$
 */
@CompileStatic
class PaginationWebObject
{
    List<PaginationPageWebObject> allPages

    Integer currentPage

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    PaginationPageWebObject previousPage

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    PaginationPageWebObject nextPage

    def withPages(Integer page, Integer totalPages, Closure<String> urlBuilder)
    {
        currentPage = page
        allPages = []

        for (int i = 1; i <= totalPages; i++) {
            allPages << new PaginationPageWebObject([
                    number: i,
                    url: urlBuilder.call(i),
                    current: i == page
            ])
        }

        if (page > 1) {
            previousPage = new PaginationPageWebObject([
                    number: page - 1,
                    url: urlBuilder.call(page - 1),
                    current: false
            ])
        }

        if (page < totalPages) {
            nextPage = new PaginationPageWebObject([
                    number: page + 1,
                    url: urlBuilder.call(page + 1),
                    current: false
            ])
        }
    }
}
