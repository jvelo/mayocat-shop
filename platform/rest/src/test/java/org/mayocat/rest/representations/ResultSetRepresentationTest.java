/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.representations;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * @version $Id$
 */
public class ResultSetRepresentationTest
{
    @Test
    public void testCreateResultSetRepresentation() throws Exception
    {
        Object[] objects = new Object[10];
        for (int i = 0; i < 10; i++) {
            objects[i] = new Object();
        }

        ResultSetRepresentation<Object> resultSet = new ResultSetRepresentation<Object>(
                "/test/",
                10, 20,
                Arrays.asList(objects),
                42
        );

        Assert.assertEquals("/test/?number=10&offset=10", resultSet.getPrevious().getHref());
        Assert.assertEquals("/test/?number=10&offset=30", resultSet.getNext().getHref());
    }
}
