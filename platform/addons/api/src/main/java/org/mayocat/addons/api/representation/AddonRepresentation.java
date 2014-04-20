/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.api.representation;

import org.mayocat.model.Addon;

/**
 * @version $Id$
 */
public class AddonRepresentation
{
    private Object value;

    private String type;

    private String source;

    private String group;

    private String key;

    public AddonRepresentation()
    {
        // No-arg constructor for Jackson de-serialization
    }

    public AddonRepresentation(Addon addon)
    {
        this.value = addon.getValue();
        this.type = addon.getType().toJson();
        this.group = addon.getGroup();
        this.source = addon.getSource().toJson();
        this.key = addon.getKey();
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }
}
