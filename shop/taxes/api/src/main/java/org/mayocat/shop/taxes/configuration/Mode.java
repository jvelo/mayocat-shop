/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.taxes.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Represents the configured mode of tax management. Inclusive of taxes mode means the user manipulates only prices
 * inclusive of taxes in its back-office, and the platform is responsible to extract the original price exclusive of
 * taxes and the amount for the defined taxes ; while exclusive of taxes means the user manipulates prices exclusive of
 * taxes, and taxes are calculated by the platform and added on top of that.
 *
 * @version $Id$
 */
public enum Mode
{
    INCLUSIVE_OF_TAXES("incl"),
    EXCLUSIVE_OF_TAXES("excl");

    private String code;

    Mode(String code)
    {
        this.code = code;
    }

    @JsonValue
    public String toJson()
    {
        return code;
    }

    @JsonCreator
    public static Mode create(String text)
    {
        for (Mode mode : values()) {
            if (mode.toJson().equals(text)) {
                return mode;
            }
        }
        return INCLUSIVE_OF_TAXES;
    }
}
