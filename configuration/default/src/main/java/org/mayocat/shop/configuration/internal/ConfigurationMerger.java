package org.mayocat.shop.configuration.internal;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.common.reflect.TypeToken;

/**
 * @version $Id$
 */
public class ConfigurationMerger
{
    public <T> T merge(T object, Map<String, Object> json)
    {
        return (T) this.mergeInternal(getConfigurationClass(object), object, json);
    }

    private Object mergeInternal(Class klass, Object object, Map<String, Object> json)
    {
        try {
            Object merged = klass.newInstance();

        } catch (InstantiationException e) {

        } catch (IllegalAccessException e) {

        }
        return object;
    }

    public <T> Class getConfigurationClass(Object object)
    {
        TypeToken<T> type = new TypeToken<T>(object.getClass()) {};
        return type.getRawType();
    }
}
