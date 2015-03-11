/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.representations;

import com.wordnik.swagger.annotations.ApiModel;
import com.wordnik.swagger.annotations.ApiModelProperty;

/**
 * @version $Id$
 */
@ApiModel(value = "Represents information about a locale")
public class LocaleRepresentation
{
    @ApiModelProperty(value = "The BPC 47 tag for this locale")
    private String tag;

    @ApiModelProperty(value = "The locale name in plain english")
    private String name;

    public LocaleRepresentation(String tag, String name)
    {
        this.tag = tag;
        this.name = name;
    }

    public String getTag()
    {
        return tag;
    }

    public String getName()
    {
        return name;
    }
}
