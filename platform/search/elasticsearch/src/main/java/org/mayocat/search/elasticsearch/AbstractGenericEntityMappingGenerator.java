package org.mayocat.search.elasticsearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
public abstract class AbstractGenericEntityMappingGenerator implements EntityMappingGenerator
{
    private static final String MAYOAPP_PATH = "mayoapp/";

    private static final String ES_PATH = MAYOAPP_PATH + "es/";

    private static final String ENTITIES_PATH = ES_PATH + "entities/";

    @Inject
    private Logger logger;

    @Override
    public Map<String, Object> generateMapping()
    {
        ObjectMapper mapper = new ObjectMapper();
        try {
            try {
                String fullJson = Resources
                        .toString(Resources.getResource(getMappingFileName(forClass())), Charsets.UTF_8);

                // There is a full mapping JSON file

                return mapper.readValue(fullJson, new TypeReference<Map<String, Object>>(){});
            } catch (IllegalArgumentException e) {

                // This means the full mapping has not been found
                try {
                    String propertiesJson = Resources
                            .toString(Resources.getResource(getPropertiesMappingFileName(forClass())), Charsets.UTF_8);

                    // There is a mapping just for properties
                    final Map<String, Object> properties =
                            mapper.readValue(propertiesJson, new TypeReference<Map<String, Object>>(){});

                    Map<String, Object> mapping = new HashMap<String, Object>();
                    mapping.put(getEntityName(forClass()), new HashMap<String, Object>()
                    {
                        {
                            // The "properties" of the product object
                            put("properties", new HashMap<String, Object>()
                            {
                                {
                                    // the "properties" property of the properties of the product object
                                    put("properties", new HashMap<String, Object>()
                                    {
                                        {
                                            // the "properties" of the property "properties"
                                            // of the properties of the product object
                                            put("properties", properties);
                                        }
                                    });
                                }
                            });
                        }
                    });

                    return mapping;
                } catch (IllegalArgumentException e1) {

                    // There is no mapping at all.
                    return null;
                }
            }
        } catch (IOException e1) {
            return null;
        }
    }

    private String getMappingFileName(Class entityClass)
    {
        return ENTITIES_PATH + getEntityName(entityClass) + ".json";
    }

    private String getPropertiesMappingFileName(Class entityClass)
    {
        return ENTITIES_PATH + getEntityName(entityClass) + "_properties.json";
    }

    private String getEntityName(Class entityClass)
    {
        return entityClass.getSimpleName().toLowerCase();
    }
}
