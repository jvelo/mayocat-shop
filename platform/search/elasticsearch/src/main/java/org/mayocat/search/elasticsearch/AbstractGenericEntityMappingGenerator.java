/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search.elasticsearch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.model.Slug;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
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
    private PlatformSettings platformSettings;

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

                Map<String, Object> mapping = mapper.readValue(fullJson, new TypeReference<Map<String, Object>>(){});

                // check if "addons" mapping present, and merge it in if not
                if (!hasAddonsMapping(mapping)) {
                    insertAddonsMapping(mapping);
                }

                return mapping;
            } catch (IllegalArgumentException e) {

                // This means the full mapping has not been found
                try {
                    String propertiesJson = Resources
                            .toString(Resources.getResource(getPropertiesMappingFileName(forClass())), Charsets.UTF_8);

                    // There is a mapping just for properties
                    final Map<String, Object> properties =
                            mapper.readValue(propertiesJson, new TypeReference<Map<String, Object>>(){});

                    Map<String, Object> mapping = new HashMap<String, Object>();

                    Map<String, Object> entity = new HashMap<String, Object>();
                    Map<String, Object> entityProperties = new HashMap<String, Object>()
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
                    };

                    if (Slug.class.isAssignableFrom(this.forClass())) {
                        entityProperties.put("slug", new HashMap<String, Object>()
                        {
                            {
                                put("index", "not_analyzed");
                                put("type", "string");
                            }
                        });
                    }

                    entity.put("properties", entityProperties);
                    mapping.put(getEntityName(forClass()), entity);

                    insertAddonsMapping(mapping);

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

    /**
     * Generates and insert the mapping for platform addon fields for this entity in the entity mapping.
     *
     * @param inEntityMapping the entity mapping to insert the addons mapping in.
     */
    protected void insertAddonsMapping(Map<String, Object> inEntityMapping)
    {
        Map<String, Object> addons = getAddonsMapping(inEntityMapping);
        if (addons == null) {
            addons = Maps.newHashMap();
            Map entityMap = (Map) inEntityMapping.get(getEntityName(forClass()));
            Map entityProductMap = (Map) entityMap.get("properties");
            entityProductMap.put("addons", addons);
        }

        addons.putAll(generateAddonsMapping());
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Generates the mapping for addons for the entity covered by the concrete class implementing this abstract class,
     * based on addons definition for the platform. Addon definition can specify elastic search field mapping using the
     * {@link ESAddonProperties#MAPPING} key.
     *
     * @return the generated addon mapping map
     */
    private Map<String, Object> generateAddonsMapping()
    {
        Map<String, Object> addonsMapping = Maps.newHashMap();
        Map<String, Object> properties = Maps.newHashMap();
        Map<String, Object> platformAddons = Maps.newHashMap();
        Map<String, Object> platformAddonsProperties = Maps.newHashMap();

        platformAddons.put("properties", platformAddonsProperties);
        properties.put("platform", platformAddons);
        addonsMapping.put("properties", properties);

        for (String groupKey : platformSettings.getAddons().keySet()) {
            AddonGroupDefinition group = platformSettings.getAddons().get(groupKey);

            Map<String, Object> groupMapping;
            Map<String, Object> groupMappingProperties;

            if (!platformAddonsProperties.containsKey(groupKey)) {
                groupMapping = Maps.newHashMap();
                groupMappingProperties = Maps.newHashMap();
                groupMapping.put("properties", groupMappingProperties);
                platformAddonsProperties.put(groupKey, groupMapping);
            } else {
                groupMapping = (Map<String, Object>) platformAddonsProperties.get(groupKey);
                groupMappingProperties = (Map<String, Object>) groupMapping.get("properties");
            }

            for (String key : group.getFields().keySet()) {
                AddonFieldDefinition addon = group.getFields().get(key);

                Map<String, Object> addonMapping;

                if (addon.getProperties().keySet().size() > 0) {
                    for (String propertyKey : addon.getProperties().keySet()) {
                        if (propertyKey.equals(ESAddonProperties.MAPPING)) {

                            // We've found a ES mapping for this addon

                            if (!groupMappingProperties.containsKey(groupKey)) {
                                addonMapping = Maps.newHashMap();
                                groupMappingProperties.put(key, addonMapping);
                            } else {
                                addonMapping = (Map<String, Object>) groupMappingProperties.get(key);
                            }

                            Map<String, Object> addonMappingDefinition =
                                    (Map<String, Object>) addon.getProperties().get(propertyKey);

                            addonMapping.putAll(addonMappingDefinition);
                        }
                    }
                }
            }
        }

        return addonsMapping;
    }

    /**
     * @param mapping the mapping map to check the presence of addon mapping for
     * @return true if the passed mapping contains an addons mapping, false otherwise
     */
    private boolean hasAddonsMapping(Map<String, Object> mapping)
    {
        return getAddonsMapping(mapping) != null;
    }

    /**
     * @param mapping the mapping map to get the addons mapping for
     * @return the addons mapping for this entity, if present, null otherwise
     */
    private Map<String, Object> getAddonsMapping(Map<String, Object> mapping)
    {
        try {
            Map productMap = (Map) mapping.get(getMappingFileName(forClass()));
            Map propertiesProductMap = (Map) productMap.get("properties");
            return (Map<String, Object>) propertiesProductMap.get("addons");
        } catch (NullPointerException e) {
            // Ignore
        }
        return null;
    }

    /**
     * @param entityClass the class to get the mapping file name for.
     * @return the full path of the file that would define the mapping for this entity
     */
    private String getMappingFileName(Class entityClass)
    {
        return ENTITIES_PATH + getEntityName(entityClass) + ".json";
    }

    /**
     * @param entityClass the class to get the mapping file name for.
     * @return the full path of the file that would define the mapping <strong>of properties only<strong>of this entity
     */
    private String getPropertiesMappingFileName(Class entityClass)
    {
        return ENTITIES_PATH + getEntityName(entityClass) + "_properties.json";
    }

    /**
     * @param entityClass the class to get the entity name for
     * @return the name of the entity the passed class represents
     */
    private String getEntityName(Class entityClass)
    {
        return entityClass.getSimpleName().toLowerCase();
    }
}
