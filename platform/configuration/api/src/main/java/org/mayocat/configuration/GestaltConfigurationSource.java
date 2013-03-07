package org.mayocat.configuration;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface GestaltConfigurationSource
{
    /**
     * @return the configuration object to serialize
     */
    Object get();
}
