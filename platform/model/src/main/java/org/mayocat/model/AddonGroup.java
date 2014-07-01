/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

/**
 * @version $Id$
 */
public class AddonGroup implements Serializable
{
    @JsonIgnore
    private UUID entityId;

    private AddonSource source;

    private String group;

    private Map<String, Map<String, Object>> model;

    private Object value;

    public UUID getEntityId()
    {
        return entityId;
    }

    public void setEntityId(UUID entityId)
    {
        this.entityId = entityId;
    }

    public AddonSource getSource()
    {
        return source;
    }

    public void setSource(AddonSource source)
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

    public Map<String, Map<String, Object>> getModel()
    {
        return model;
    }

    public void setModel(Map<String, Map<String, Object>> model)
    {
        this.model = model;
    }

    public Object getValue()
    {
        return value;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }
}
