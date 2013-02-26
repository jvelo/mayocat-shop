package org.mayocat.configuration;

import java.util.Map;

import org.xwiki.component.annotation.Role;

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
