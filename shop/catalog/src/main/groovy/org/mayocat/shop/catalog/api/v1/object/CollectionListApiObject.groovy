package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.rest.api.object.BasePaginatedListApiObject

/**
 * API object for a list of collections
 *
 * @version $Id$
 */
@CompileStatic
class CollectionListApiObject extends BasePaginatedListApiObject
{
    List<CollectionApiObject> collections;
}
