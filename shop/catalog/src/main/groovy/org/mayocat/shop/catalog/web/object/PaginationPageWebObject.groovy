package org.mayocat.shop.catalog.web.object

import groovy.transform.CompileStatic

/**
 * Web object for a single page. See {@link PaginationWebObject}
 *
 * @version $Id$
 */
@CompileStatic
class PaginationPageWebObject
{
    Boolean current = false

    Integer number

    String url
}
