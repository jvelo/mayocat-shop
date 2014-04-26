/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.internal;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import org.mayocat.configuration.general.GeneralSettings;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
public class AbstractConfigurationTest
{
    protected Map<String, Serializable> loadConfiguration(String uri) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(loadJSON(uri), new TypeReference<Map<String, Object>>(){});
    }

    private String loadJSON(String uri) throws IOException
    {
        return Resources.toString(Resources.getResource(uri), Charsets.UTF_8);
    }

    protected Map<String, Serializable> getConfiguration(GeneralSettings configuration) throws Exception
    {
        ObjectMapper mapper = new ObjectMapper();
        String asJson = mapper.writeValueAsString(configuration);
        Map<String, Serializable> configurationAsJson = mapper.readValue(asJson, new TypeReference<Map<String, Object>>(){});
        return configurationAsJson;
    }
}
