package org.mayocat.shop.store.rdbms.dbi.extraction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.mayocat.shop.store.rdbms.dbi.dao.util.StringUtil;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class EntityExtractor<E>
{
    public E extract(Map<String, Object> entityData, Class<?> type)
    {
        E entity;
        try {
            entity = (E) type.newInstance();
            String entityType = type.getSimpleName().toLowerCase();
            // TODO we will likely need to support custom table name mapping via annotation in the future

            for (Method method : entity.getClass().getMethods()) {
                if (method.getName().startsWith("set") && !method.getName().equals("setTranslations")
                        && Character.isUpperCase(method.getName().charAt(3)))
                {
                    // Found a setter.
                    String field = StringUtil.snakify(method.getName().substring(3));

                    Object value = null;
                    if (entityData.containsKey("entity." + field)) {
                        value = entityData.get("entity." + field);
                    } else if (entityData.containsKey(entityType + "." + field)) {
                        value = entityData.get(entityType + "." + field);
                    }

                    boolean setterAccessible = method.isAccessible();
                    method.setAccessible(true);
                    method.invoke(entity, value);
                    method.setAccessible(setterAccessible);
                }
            }
            return entity;
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
