/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.json;

import org.junit.Test;
import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import static org.junit.Assert.assertEquals;

/**
 * @version $Id$
 */
public class ConfigurableTest
{
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testParseConfigurable() throws Exception
    {
        Configurable<Boolean> value1 =
                mapper.readValue("{\"default\": true, \"configurable\":false}", Configurable.class);
        assertEquals(value1.isConfigurable(), false);
        assertEquals(value1.getDefaultValue(), true);
    }

    @Test
    public void testDeserializeConfigurableDouble() throws Exception
    {
        Configurable<Double> value1 =
                mapper.readValue("{\"default\": 4.20, \"configurable\":false}", Configurable.class);
        assertEquals(value1.isConfigurable(), false);
        assertEquals(value1.getDefaultValue(), new Double(4.20));

        Configurable<Double> value2 =
                mapper.readValue("{\"default\": 3.14, \"configurable\":true}", Configurable.class);
        assertEquals(value2.isConfigurable(), true);
        assertEquals(value2.getDefaultValue(), new Double(3.14));
    }

    @Test
    public void testDeserializeConfigurableListOfString() throws Exception
    {
        Configurable<Double> value1 =
                mapper.readValue("{\"default\": [\"fr\", \"en\"], \"configurable\":false}", Configurable.class);
        assertEquals(value1.isConfigurable(), false);
        assertEquals(value1.getDefaultValue(), Lists.newArrayList("fr", "en"));

        Configurable<Double> value2 =
                mapper.readValue("{\"default\": [\"de\", \"ro\"], \"configurable\":true}", Configurable.class);
        assertEquals(value2.isConfigurable(), true);
        assertEquals(value2.getDefaultValue(), Lists.newArrayList("de", "ro"));
    }

    @Test
    public void testVisibility() throws Exception
    {
        Configurable<String> value1 =
                mapper.readValue("{\"default\": \"Nana\"}", Configurable.class);
        assertEquals(value1.isVisible(), true);

        Configurable<String> value2 =
                mapper.readValue("{\"default\": \"Nana\", \"configurable\":false}", Configurable.class);
        assertEquals(value2.isVisible(), false);

        Configurable<String> value3 =
                mapper.readValue("{\"default\": \"Nana\", \"configurable\":false, \"visible\" : true}", Configurable.class);
        assertEquals(value3.isVisible(), true);

    }
}
