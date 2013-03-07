package org.mayocat.configuration.json;

import java.util.Map;

import org.junit.Test;
import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.jackson.GestaltConfigurationModule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;

import junit.framework.Assert;

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
