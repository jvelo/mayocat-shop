/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.dao.util;

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
