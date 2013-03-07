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
