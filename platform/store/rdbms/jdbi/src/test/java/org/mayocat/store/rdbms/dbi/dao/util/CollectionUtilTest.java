/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.dao.util;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

import junit.framework.Assert;

/**
 * @version $Id$
 */
public class CollectionUtilTest
{
    @Test
    public void testMoveDown()
    {
        List<String> list = Lists.newArrayList("one", "two", "three", "four", "five");
        CollectionUtil.move(list, 1, 3);
        Assert.assertEquals("one", list.get(0));
        Assert.assertEquals("three", list.get(1));
        Assert.assertEquals("four", list.get(2));
        Assert.assertEquals("two", list.get(3));
        Assert.assertEquals("five", list.get(4));

        list = Lists.newArrayList("one", "two", "three", "four", "five");
        CollectionUtil.move(list, 0, 4);
        Assert.assertEquals("two", list.get(0));
        Assert.assertEquals("three", list.get(1));
        Assert.assertEquals("four", list.get(2));
        Assert.assertEquals("five", list.get(3));
        Assert.assertEquals("one", list.get(4));
    }

    @Test
    public void testMoveUp()
    {
        List<String> list = Lists.newArrayList("one", "two", "three", "four", "five");
        CollectionUtil.move(list, 3, 1);
        Assert.assertEquals("one", list.get(0));
        Assert.assertEquals("four", list.get(1));
        Assert.assertEquals("two", list.get(2));
        Assert.assertEquals("three", list.get(3));
        Assert.assertEquals("five", list.get(4));

        list = Lists.newArrayList("one", "two", "three", "four", "five");
        CollectionUtil.move(list, 4, 0);
        Assert.assertEquals("five", list.get(0));
        Assert.assertEquals("one", list.get(1));
        Assert.assertEquals("two", list.get(2));
        Assert.assertEquals("three", list.get(3));
        Assert.assertEquals("four", list.get(4));
    }
}
