package org.mayocat.shop.service.internal;

import java.io.IOException;
import java.util.Map;

import org.mayocat.shop.configuration.general.GeneralConfiguration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
public class AbstractConfigurationTest
{
    protected Map<String, Object> loadConfiguration(String uri) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(loadJSON(uri), new TypeReference<Map<String, Object>>(){});
    }

    private String loadJSON(String uri) throws IOException
    {
        return Resources.toString(Resources.getResource(uri), Charsets.UTF_8);
    }

    protected Map<String, Object> getConfiguration(GeneralConfiguration configuration) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String asJson = mapper.writeValueAsString(configuration);
        Map<String, Object> configurationAsJson = mapper.readValue(asJson, new TypeReference<Map<String, Object>>(){});
        return configurationAsJson;
    }
}
