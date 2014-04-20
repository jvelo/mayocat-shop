/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.dao.util;

import org.junit.Test;
import org.mayocat.util.StringUtil;

import junit.framework.Assert;

/**
 * @version $Id$
 */
public class StringUtilTest
{
    @Test
    public void testSnakify()
    {
        Assert.assertEquals("foo", StringUtil.snakify("foo"));
        Assert.assertEquals("its_fun_to_stay_at_the_ymca", StringUtil.snakify("itsFunToStayAtTheYMCA"));
    }
}
