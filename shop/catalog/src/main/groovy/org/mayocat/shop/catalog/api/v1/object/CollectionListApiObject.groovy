package org.mayocat.shop.catalog.api.v1.object

/**
 * API object for a list of collections
 *
 * @version $Id$
 */
class CollectionListApiObject extends BasePaginatedListApiObject
{
    List<CollectionApiObject> collections;
}
