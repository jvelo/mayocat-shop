package org.mayocat.shop.configuration;

import java.lang.reflect.Type;
import java.util.Map;

import org.mayocat.shop.configuration.shop.ShopConfiguration;
import org.xwiki.component.annotation.Role;

import com.google.common.collect.Multimap;

/**
 * @version $Id$
 */
@Role
public interface ConfigurationService
{
    Map<Class, Object> getConfigurations();

    Object getConfiguration(Class c);

    Map<String, Object> getConfigurationAsJson();

    Map<String, Object> getConfigurationAsJson(String moduleName) throws NoSuchModuleException;

    void updateConfiguration(Map<String, Object> configuration);

    void updateConfiguration(String module, Map<String, Object> configuration) throws NoSuchModuleException;
}
