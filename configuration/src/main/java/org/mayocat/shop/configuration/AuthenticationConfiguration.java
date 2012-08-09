package org.mayocat.shop.configuration;

import javax.validation.Valid;

import org.codehaus.jackson.annotate.JsonProperty;

public class AuthenticationConfiguration
{
    @Valid
    @JsonProperty
    String cookieEncryptionKey;

    @Valid
    @JsonProperty
    Integer passwordSaltLogRounds = 10;

    public String getCookieEncryptionKey()
    {
        return cookieEncryptionKey;
    }

    public Integer getPasswordSaltLogRounds()
    {
        return passwordSaltLogRounds;
    }
}
