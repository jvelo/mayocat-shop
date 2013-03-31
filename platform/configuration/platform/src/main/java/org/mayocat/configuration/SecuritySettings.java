package org.mayocat.configuration;

import javax.validation.Valid;

import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SecuritySettings
{
    @Valid
    @NotBlank
    @JsonProperty
    String encryptionKey;

    @Valid
    @NotBlank
    String signingKey;

    @Valid
    @JsonProperty
    Integer passwordSaltLogRounds = 10;

    public String getEncryptionKey()
    {
        return encryptionKey;
    }

    public String getSigningKey()
    {
        return signingKey;
    }

    public Integer getPasswordSaltLogRounds()
    {
        return passwordSaltLogRounds;
    }
}
