package org.mayocat.search.elasticsearch.internal;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.model.Addon;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.PerhapsLoaded;
import org.mayocat.model.annotation.DoNotIndex;
import org.mayocat.model.annotation.Index;
import org.mayocat.search.elasticsearch.EntitySourceExtractor;
import org.mayocat.store.AttachmentStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("default")
public class DefaultEntitySourceExtractor implements EntitySourceExtractor
{
    @Inject
    private Logger logger;

    @Inject
    private AttachmentStore attachmentStore;

    @Override
    public Map<String, Object> extract(Entity entity)
    {
        Map<String, Object> source = new HashMap<String, Object>();

        boolean hasClassLevelIndexAnnotation = entity.getClass().isAnnotationPresent(Index.class);

        for (Field field : entity.getClass().getDeclaredFields()) {
            boolean isAccessible = field.isAccessible();
            try {
                if (Modifier.isStatic(field.getModifiers())) {
                    // we're not interested in static fields like serialVersionUid (or any other static field)
                    continue;
                }

                field.setAccessible(true);
                boolean searchIgnoreFlag = field.isAnnotationPresent(DoNotIndex.class);
                boolean indexFlag = field.isAnnotationPresent(Index.class);

                if ((hasClassLevelIndexAnnotation || indexFlag) && !searchIgnoreFlag) {
                    if (field.get(entity) != null) {
                        Class fieldClass = field.get(entity).getClass();

                        if (isAddonField(fieldClass, field)) {
                            PerhapsLoaded<List<Addon>> addons = (PerhapsLoaded<List<Addon>>) field.get(entity);
                            if (addons.isLoaded()) {
                                List<Map> addonsSource = Lists.newArrayList();
                                ObjectMapper mapper = new ObjectMapper();
                                for (Addon addon : addons.get()) {
                                    addonsSource.add(mapper.readValue(mapper.writeValueAsString(addon), Map.class));
                                }
                                source.put(field.getName(), addonsSource);
                            }
                        } else if (BigDecimal.class.isAssignableFrom(fieldClass)) {
                            source.put(field.getName(), ((BigDecimal) field.get(entity)).doubleValue());
                        } else {
                            source.put(field.getName(), field.get(entity));
                        }
                    } else {
                        source.put(field.getName(), null);
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

        if (HasFeaturedImage.class.isAssignableFrom(entity.getClass())) {
            HasFeaturedImage hasFeaturedImage = (HasFeaturedImage) entity;
            if (hasFeaturedImage.getFeaturedImageId() != null) {
                Attachment attachment = attachmentStore.findById(hasFeaturedImage.getFeaturedImageId());
                if (attachment != null) {
                    Map<String, Object> imageMap = Maps.newHashMap();
                    imageMap.put("url", "/image/" + attachment.getSlug() + "." + attachment.getExtension());
                    imageMap.put("title", attachment.getTitle());
                    imageMap.put("extension", attachment.getExtension());
                    source.put("featuredImage", imageMap);
                }
            }
        }

        return source;
    }

    private boolean isAddonField(Class fieldClass, Field field)
    {
        try {
            if (PerhapsLoaded.class.isAssignableFrom(fieldClass)) {
                ParameterizedType type = (ParameterizedType) field.getGenericType();
                if (type.getActualTypeArguments().length > 0) {
                    ParameterizedType listType = (ParameterizedType) type.getActualTypeArguments()[0];
                    if (listType.getRawType().equals(List.class) && listType.getActualTypeArguments().length > 0) {
                        if (listType.getActualTypeArguments()[0].equals(Addon.class)) {
                            return true;
                        }
                    }
                }
            }
            return false;
        } catch (ClassCastException e) {
            return false;
        }
    }
}
