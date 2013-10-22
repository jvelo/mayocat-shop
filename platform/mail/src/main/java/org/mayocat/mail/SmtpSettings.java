package org.mayocat.mail;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class SmtpSettings
{
    @JsonProperty
    private String server;

    @JsonProperty
    private Integer port = 587;

    @JsonProperty
    private Optional<String> username;

    @JsonProperty
    private Optional<String> password;

    @JsonProperty
    private Map<String, String> properties = Maps.newHashMap();

    public String getServer()
    {
        return server;
    }

    public Integer getPort()
    {
        return port;
    }

    public Optional<String> getUsername()
    {
        return username;
    }

    public Optional<String> getPassword()
    {
        return password;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }
}
