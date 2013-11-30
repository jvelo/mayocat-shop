/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store.rdbms.dbi.jointype;

import java.util.Map;

/**
 * @version $Id$
 */
public class EntityAndCountsJoinRow
{
    private Map<String, Object> entityData;

    private Map<String, Long> counts;

    public Map<String, Long> getCounts()
    {
        return counts;
    }

    public void setCounts(Map<String, Long> counts)
    {
        this.counts = counts;
    }

    public Map<String, Object> getEntityData()
    {
        return entityData;
    }

    public void setEntityData(Map<String, Object> entityData)
    {
        this.entityData = entityData;
    }
}
