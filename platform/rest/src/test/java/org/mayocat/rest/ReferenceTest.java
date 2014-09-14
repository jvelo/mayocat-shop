/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest;

import javax.ws.rs.WebApplicationException;

import org.junit.Assert;
import org.junit.Test;

/**
 * @version $Id$
 */
public class ReferenceTest
{
    @Test
    public void testDeserializeReference()
    {
        Reference ref = Reference.valueOf("entity@tenant");
        Assert.assertEquals("entity", ref.getEntitySlug());
        Assert.assertEquals("tenant", ref.getTenantSlug());

        ref = Reference.valueOf("e@t");
        Assert.assertEquals("e", ref.getEntitySlug());
        Assert.assertEquals("t", ref.getTenantSlug());
    }

    @Test(expected = WebApplicationException.class)
    public void testDeserializeInvalidReference1()
    {
        Reference.valueOf("@tenant");
    }

    @Test(expected = WebApplicationException.class)
    public void testDeserializeInvalidReference2()
    {
        Reference.valueOf("entity@");
    }

    @Test(expected = WebApplicationException.class)
    public void testDeserializeInvalidReference3()
    {
        Reference.valueOf("@");
    }

    @Test(expected = WebApplicationException.class)
    public void testDeserializeInvalidReference4()
    {
        Reference.valueOf("entity");
    }
}
