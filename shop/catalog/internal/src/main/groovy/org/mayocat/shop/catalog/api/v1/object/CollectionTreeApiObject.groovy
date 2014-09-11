package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic
import org.mayocat.rest.api.object.BaseApiObject

/**
 * @version $Id$
 */
@CompileStatic
class CollectionTreeApiObject extends BaseApiObject
{
    List<CollectionItemApiObject> collections
}
