/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.localization.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.mayocat.context.WebContext;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Addon;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.model.HasAddons;
import org.mayocat.model.Localized;
import org.mayocat.model.annotation.LocalizedField;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * @version $Id$
 */
@Component
public class DefaultEntityLocalizationService implements EntityLocalizationService
{
    @Inject
    private org.slf4j.Logger logger;

    @Inject
    private WebContext context;

    @Override
    public <T extends Localized> T localize(T entity)
    {
        if (this.context == null || !this.context.isAlternativeLocale()) {
            return entity;
        }
        else {
            return localize(entity, this.context.getLocale());
        }
    }

    @Override
    public <T extends Localized> T localize(T entity, Locale locale)
    {
        if (locale == null || entity.getLocalizedVersions() == null ||
                !entity.getLocalizedVersions().containsKey(locale))
        {
            return entity;
        }

        T copiedEntity = SerializationUtils.clone(entity);

        if (copiedEntity == null) {
            return entity;
        }

        // Special I/O case for attachment : set back the input stream manually
        if (copiedEntity instanceof Attachment) {
            ((Attachment) copiedEntity).setData(((Attachment) entity).getData());
        }

        // Handle entity fields :
        // - loops over methods, checking for setters, then for each one of them
        // - infer a field name from found setter
        // - check if that field has a "LocalizedField" annotation, if not ignore
        // - if it does, try to find this field in the locale's map of translations
        // - if found and not null or empty string use the setter to set this as the localized field value

        for (Method method : copiedEntity.getClass().getDeclaredMethods()) {
            if (method.getName().startsWith("set") && Character.isUpperCase(method.getName().charAt(3))) {
                // Found a setter.
                if (method.getName().length() <=  4) {
                    continue;
                }
                String fieldName = method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4);

                Field field = null;
                try {
                    field = copiedEntity.getClass().getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    this.logger.debug("Cannot find field for setter {}", method.getName());
                }

                // Check if either field or method has a "LocalizedField" annotation
                if (field != null && (field.isAnnotationPresent(LocalizedField.class) ||
                        method.isAnnotationPresent(LocalizedField.class)))
                {
                    Object value = null;
                    if (copiedEntity.getLocalizedVersions().get(locale).containsKey(fieldName)) {

                        value = copiedEntity.getLocalizedVersions().get(locale).get(fieldName);

                        if (String.class.isAssignableFrom(value.getClass()) && Strings.isNullOrEmpty((String) value)) {
                            // Ignore empty strings, consider them as nulls
                            continue;
                        }

                        boolean setterAccessible = method.isAccessible();
                        method.setAccessible(true);
                        try {
                            method.invoke(copiedEntity, value);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            logger.error("Cannot set property {}", field.getName());
                        }

                        method.setAccessible(setterAccessible);
                    }
                }
            }
        }

        // Handle entity addons :
        // - check if entity has addons and those addons are loaded, and the localized version contains something
        // for addons
        // - if yes, then loop over all the entity addons, and for each :
        // - try to find the equivalent addon in the map of translation
        // - if found, and its value is not null or empty string, replace the addon value by the localized one

        if (hasLoadedAddons(copiedEntity) && copiedEntity.getLocalizedVersions().get(locale).containsKey("addons")) {
            List<Addon> entityAddons = ((HasAddons) copiedEntity).getAddons().get();
            List<Map<String, Object>> localizedAddons =
                    (List<Map<String, Object>>) copiedEntity.getLocalizedVersions().get(locale).get("addons");

            for (Addon addon : entityAddons) {
                Optional<Map<String, Object>> localized = findLocalizedAddon(addon, localizedAddons);
                if (localized.isPresent()) {
                    Object value = localized.get().get("value");

                    if (value == null ||
                            (String.class.isAssignableFrom(value.getClass()) && Strings.isNullOrEmpty((String) value)))
                    {
                        // Ignore empty strings, consider them as nulls
                        continue;
                    }

                    addon.setValue(value);
                }
            }
        }

        return copiedEntity;
    }

    private boolean hasLoadedAddons(Entity entity)
    {
        return HasAddons.class.isAssignableFrom(entity.getClass()) && ((HasAddons) entity).getAddons().isLoaded();
    }

    private Optional<Map<String, Object>> findLocalizedAddon(Addon addon, List<Map<String, Object>> localizedAddons)
    {
        for (Map<String, Object> localized : localizedAddons) {
            if (addon.getSource().toJson().equals(localized.get("source"))
                    && addon.getGroup().equals(localized.get("group"))
                    && addon.getKey().equals(localized.get("key")))
            {

                return Optional.of(localized);
            }
        }
        return Optional.absent();
    }

}
