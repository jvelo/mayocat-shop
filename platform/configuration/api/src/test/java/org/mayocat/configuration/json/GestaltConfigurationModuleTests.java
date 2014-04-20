/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.json;

import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.jackson.GestaltConfigurationModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class GestaltConfigurationModuleTests
{

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testSerializeConfigurable() throws Exception
    {
        mapper.registerModule(new GestaltConfigurationModule());

        Configurable<Integer> test = new Configurable<Integer>(42);
        Map<String, Configurable<Integer>> data = Maps.newHashMap();
        data.put("foo", test);

        String serialized = mapper.writeValueAsString(data);

        Assert.assertEquals("{\"foo\":42}", serialized);
    }
}
