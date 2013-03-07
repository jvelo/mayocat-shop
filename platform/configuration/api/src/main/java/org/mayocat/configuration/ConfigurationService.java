package org.mayocat.configuration;

import java.util.Map;

import org.mayocat.base.ExposedSettings;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ConfigurationService
{
    Map<Class, Object> getSettings();

    <T extends ExposedSettings> T getSettings(Class<T> c);

    void updateSettings(Map<String, Object> configuration);

    void updateSettings(String module, Map<String, Object> configuration) throws NoSuchModuleException;

    Map<String, Object> getSettingsAsJson();

    Map<String, Object> getSettingsAsJson(String moduleName) throws NoSuchModuleException;

    //

    Map<String, Object> getGestaltConfiguration();
}
