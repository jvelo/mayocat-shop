/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
