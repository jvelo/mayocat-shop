package org.mayocat.store.rdbms.dbi.extraction;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import org.mayocat.util.StringUtil;

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

            for (Method method : entity.getClass().getMethods()) {
                if (method.getName().startsWith("set") && !method.getName().equals("setTranslations")
                        && Character.isUpperCase(method.getName().charAt(3)))
                {
                    // Found a setter.
                    String field = StringUtil.snakify(method.getName().substring(3));

                    Object value = null;
                    if (entityData.containsKey(field)) {
                        value = entityData.get(field);
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
