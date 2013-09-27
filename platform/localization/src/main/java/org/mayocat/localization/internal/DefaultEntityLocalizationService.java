package org.mayocat.localization.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javax.inject.Inject;

import org.apache.commons.lang3.SerializationUtils;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Localized;
import org.mayocat.model.annotation.LocalizedField;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultEntityLocalizationService implements EntityLocalizationService
{
    @Inject
    private org.slf4j.Logger logger;

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
        return copiedEntity;
    }
}
