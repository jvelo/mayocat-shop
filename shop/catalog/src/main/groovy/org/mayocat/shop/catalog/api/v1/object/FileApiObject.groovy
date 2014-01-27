package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.TypeChecked

/**
 * Represents a file API object
 *
 * @version $Id$
 */
@TypeChecked
class FileApiObject extends BaseApiObject
{
    String fileName

    String extension
}
