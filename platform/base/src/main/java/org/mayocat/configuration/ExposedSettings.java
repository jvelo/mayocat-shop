package org.mayocat.configuration;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @version $Id$
 */
public interface ExposedSettings
{
    @JsonIgnore
    String getKey();
}
