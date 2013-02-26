package org.mayocat.configuration;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ConfigurationSource
{
    /**
     * @return the configuration object to serialize
     */
    Object get();
}
