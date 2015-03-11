/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.addons.model.AddonFieldDefinition;
import org.mayocat.addons.web.AddonFieldValueWebObject;
import org.mayocat.attachment.AttachmentLoadingOptions;
import org.mayocat.context.WebContext;
import org.mayocat.entity.EntityData;
import org.mayocat.entity.EntityDataLoader;
import org.mayocat.entity.EntityLoader;
import org.mayocat.entity.StandardOptions;
import org.mayocat.model.Entity;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
@Component("entityList")
public class EntityListAddonTransformer implements AddonFieldTransformer
{
    @Inject
    private ComponentManager componentManager;

    @Inject
    private Map<String, EntityLoader> entityLoader;

    @Inject
    private EntityDataLoader dataLoader;

    @Inject
    private Logger logger;

    @Inject
    private WebContext webContext;

    private Map<String, EntityListAddonWebObjectBuilder> builders;

    public Optional<AddonFieldValueWebObject> toWebView(EntityData<?> entityData,
            AddonFieldDefinition definition, Object storedValue)
    {
        List<String> slugs = (List) storedValue;
        if (!definition.getProperties().containsKey("entityList.entityType")) {
            logger.warn("entityList.entityType property is mandatory for addon type entityList");
            return Optional.absent();
        }

        String type = (String) definition.getProperties().get("entityList.entityType");

        if (!entityLoader.containsKey(type)) {
            logger.warn("No loader for entity type " + type);
            return Optional.absent();
        }

        String builderHint = null;
        if (webContext.getTenant() == null) {
            builderHint = "global/" + type;
        } else {
            builderHint = type;
        }

        if (!getBuilders().containsKey(builderHint)) {
            logger.warn("No entity list addon web object buider for hint " + builderHint);
            return Optional.absent();
        }

        EntityListAddonWebObjectBuilder builder = getBuilders().get(builderHint);
        EntityLoader loader = entityLoader.get(type);

        List<Entity> entities = Lists.newArrayList();

        try {
            for (String slug : slugs) {
                Entity e;
                if (slug.indexOf('@') > 0 && webContext.getTenant() == null) {
                    String tenantSlug = StringUtils.substringAfter(slug, "@");
                    String entitySlug = StringUtils.substringBefore(slug, "@");
                    e = loader.load(entitySlug, tenantSlug);
                } else {
                    e = loader.load(slug);
                }
                entities.add(e);
            }

            List<EntityData<Entity>> data = dataLoader.load(entities, StandardOptions.LOCALIZE,
                    AttachmentLoadingOptions.FEATURED_IMAGE_ONLY);

            List<Object> result = Lists.newArrayList();

            for (EntityData<Entity> e : data) {
                result.add(builder.build(e));
            }

            return Optional.of(new AddonFieldValueWebObject(result, buildDisplay(data)));
        } catch (Exception e) {
            logger.warn("Exception while trying to load entity", e.getMessage());
            throw e;
            //return Optional.absent();
        }
    }

    private Map<String, EntityListAddonWebObjectBuilder> getBuilders()
    {
        if (builders != null) {
            return builders;
        }
        try {
            builders = componentManager.getInstanceMap(EntityListAddonWebObjectBuilder.class);
            return builders;
        } catch (ComponentLookupException e) {
            throw new RuntimeException("Failed to initialize entity list addon transformer", e);
        }
    }

    private Object buildDisplay(List<EntityData<Entity>> data)
    {
        StringBuilder builder = new StringBuilder();
        builder.append("<ul>");
        for (EntityData<Entity> d : data) {
            builder.append("<li>");
            builder.append(d.getEntity().getSlug());
            builder.append("</li>");
        }
        builder.append("</ul>");
        return builder.toString();
    }
}
