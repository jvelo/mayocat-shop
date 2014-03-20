package org.mayocat.rest.api.object

import groovy.transform.CompileStatic

/**
 * Represents a file API object
 *
 * @version $Id$
 */
@CompileStatic
class FileApiObject extends BaseApiObject
{
    String fileName

    String extension
}
