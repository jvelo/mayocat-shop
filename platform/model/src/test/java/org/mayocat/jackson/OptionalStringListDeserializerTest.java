/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.jackson;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class OptionalStringListDeserializerTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testDeserializeOptionalStringListWhenList() throws Exception
    {
        TestBean deserialized = mapper.readValue("{ \"foo\": [\"bar\", \"beer\"] }", TestBean.class);
        List<String> list = Lists.newArrayList("bar", "beer");
        Optional<List<String>> expected = Optional.of(list);

        Assert.assertTrue(deserialized.getFoo().isPresent());
        Assert.assertEquals(expected, deserialized.getFoo());
    }

    @Test
    public void testDeserializeOptionalStringListWhenString() throws Exception
    {
        TestBean deserialized = mapper.readValue("{ \"foo\": \"bar\" }", TestBean.class);
        List<String> list = Lists.newArrayList("bar");
        Optional<List<String>> expected = Optional.of(list);

        Assert.assertTrue(deserialized.getFoo().isPresent());
        Assert.assertEquals(expected, deserialized.getFoo());
    }

    @Test
    public void testDeserializeOptionalStringListWhenAbsent() throws Exception
    {
        TestBean deserialized = mapper.readValue("{ \"bar\": \"baz\" }", TestBean.class);
        Assert.assertFalse(deserialized.getFoo().isPresent());
    }
}
