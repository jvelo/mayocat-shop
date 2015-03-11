/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts;

import javax.validation.Valid;

import org.mayocat.configuration.Configurable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class PasswordRequirementsSettings
{
    /**
     * Minimal length (number of characters) a password must have to be considered valid.
     */
    @Valid
    @JsonProperty
    private Integer minimalLength = 8;

    private Optional<Integer> minimalEntropyBits = Optional.absent();

    public Integer getMinimalLength()
    {
        return minimalLength;
    }

    public Optional<Integer> getMinimalEntropyBits()
    {
        return minimalEntropyBits;
    }
}
