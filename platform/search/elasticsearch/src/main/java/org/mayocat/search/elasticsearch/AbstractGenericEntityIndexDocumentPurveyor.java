/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search.elasticsearch;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.elasticsearch.common.collect.Maps;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.context.WebContext;
import org.mayocat.model.AddonGroup;
import org.mayocat.model.Association;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.Identifiable;
import org.mayocat.model.Slug;
import org.mayocat.model.annotation.DoNotIndex;
import org.mayocat.model.annotation.Index;
import org.mayocat.search.EntityIndexDocumentPurveyor;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.url.EntityURLFactory;
import org.mayocat.url.URLType;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @version $Id$
 */
public abstract class AbstractGenericEntityIndexDocumentPurveyor<E extends Entity> implements
        EntityIndexDocumentPurveyor<E>
{
    public AbstractGenericEntityIndexDocumentPurveyor()
    {
    }

    @Inject
    private Logger logger;

    @Inject
    private WebContext context;

    @Inject
    private AttachmentStore attachmentStore;

    @Inject
    private EntityURLFactory entityURLFactory;

    @Override
    public Map<String, Object> purveyDocument(E entity)
    {
        return this.purveyDocument(entity, context.getTenant());
    }

    @Override
    public Map<String, Object> purveyDocument(E entity, Tenant tenant)
    {
        return extractSourceFromEntity(entity, tenant);
    }

    protected Map<String, Object> extractSourceFromEntity(Entity entity, Tenant tenant)
    {
        Map<String, Object> source = Maps.newHashMap();
        Map<String, Object> properties = Maps.newHashMap();

        boolean hasClassLevelIndexAnnotation = entity.getClass().isAnnotationPresent(Index.class);

        for (Field field : entity.getClass().getDeclaredFields()) {
            boolean isAccessible = field.isAccessible();
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    // we're not interested in static fields like serialVersionUid (or any other static field)
                    continue;
                }

                if (Identifiable.class.isAssignableFrom(entity.getClass()) && field.getName().equals("id")) {
                    // do not index the id of identifiable under properties
                    continue;
                }

                if (Slug.class.isAssignableFrom(entity.getClass()) && field.getName().equals("slug")) {
                    // do not index the slug of slugs under properties
                    // (it is indexed as a top level property)
                    continue;
                }

                field.setAccessible(true);
                boolean searchIgnoreFlag = field.isAnnotationPresent(DoNotIndex.class);
                boolean indexFlag = field.isAnnotationPresent(Index.class);

                if ((hasClassLevelIndexAnnotation || indexFlag) && !searchIgnoreFlag) {
                    if (field.get(entity) != null) {
                        Class fieldClass = field.get(entity).getClass();

                        if (isAddonField(fieldClass, field)) {

                            // Treat addons differently.
                            // They're not located in the "properties" object like the other entity properties,
                            // but they're stored in their own "addon" object

                            Association<Map<String, AddonGroup>> addons = (Association<Map<String, AddonGroup>>) field.get(entity);
                            if (addons.isLoaded()) {
                                source.put("addons", extractAddons(addons.get()));
                            }
                        } else if (BigDecimal.class.isAssignableFrom(fieldClass)) {

                            // Convert big decimal to double value

                            properties.put(field.getName(), ((BigDecimal) field.get(entity)).doubleValue());
                        } else {

                            // General case o>

                            properties.put(field.getName(), field.get(entity));
                        }
                    } else {
                        properties.put(field.getName(), null);
                    }
                }
            } catch (IllegalAccessException e) {
                this.logger.error("Error extracting entity", entity.getSlug(), e);
            } catch (JsonMappingException e) {
                this.logger.error("Error extracting entity", entity.getSlug(), e);
            } catch (JsonParseException e) {
                this.logger.error("Error extracting entity", entity.getSlug(), e);
            } catch (JsonProcessingException e) {
                this.logger.error("Error extracting entity", entity.getSlug(), e);
            } catch (IOException e) {
                this.logger.error("Error extracting entity", entity.getSlug(), e);
            } finally {
                field.setAccessible(isAccessible);
            }
        }

        source.put("properties", properties);

        if (HasFeaturedImage.class.isAssignableFrom(entity.getClass())) {
            HasFeaturedImage hasFeaturedImage = (HasFeaturedImage) entity;
            if (hasFeaturedImage.getFeaturedImageId() != null) {
                Attachment attachment = attachmentStore.findById(hasFeaturedImage.getFeaturedImageId());
                if (attachment != null) {
                    Map<String, Object> imageMap = Maps.newHashMap();
                    imageMap.put("url", entityURLFactory.create(attachment, tenant));
                    imageMap.put("title", attachment.getTitle());
                    imageMap.put("extension", attachment.getExtension());
                    source.put("featuredImage", imageMap);
                }
            }
        }

        source.put("url", entityURLFactory.create(entity, tenant).toString());
        source.put("api_url", entityURLFactory.create(entity, tenant, URLType.API).toString());
        source.put("slug", entity.getSlug());

        return source;
    }

    private Map<String, Object> extractAddons(Map<String, AddonGroup> addons) throws IOException
    {
        //TODO
        return new HashMap<>();
    }

    private boolean isAddonField(Class fieldClass, Field field)
    {
        // TODO
        return false;
    }
}
