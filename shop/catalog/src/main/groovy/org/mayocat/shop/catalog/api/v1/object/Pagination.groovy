package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.TypeChecked

/**
 * Holds pagination information
 *
 * @version $Id$
 */
@TypeChecked
class Pagination
{
    Integer numberOfItems

    Integer returnedItems

    Integer offset

    Integer totalItems

    String urlTemplate
}
