package org.mayocat.shop.configuration;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

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
