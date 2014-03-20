package org.mayocat.rest.api.object

import groovy.transform.CompileStatic

/**
 * Holds pagination information
 *
 * @version $Id$
 */
@CompileStatic
class Pagination
{
    Integer numberOfItems

    Integer returnedItems

    Integer offset

    Integer totalItems

    String urlTemplate

    Map<String, String> urlArguments = [:]
}
