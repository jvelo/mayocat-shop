package org.mayocat.shop.store.rdbms.dbi.dao.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @version $Id$
 */
public class CollectionUtil
{
    public static void move(List<?> collection, int indexToMoveFrom, int indexToMoveAt)
    {
        if (indexToMoveAt >= indexToMoveFrom) {
            Collections.rotate(collection.subList(indexToMoveFrom, indexToMoveAt + 1), -1);
        } else {
            Collections.rotate(collection.subList(indexToMoveAt, indexToMoveFrom + 1), 1);
        }
    }
}
