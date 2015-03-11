/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;

/**
 * A qualified reference to an entity, providing both the entity slug and its tenant owner slug.
 *
 * @version $Id$
 */
public class Reference
{
    private final String entitySlug;

    private final String tenantSlug;

    public Reference(String slug, String tenantSlug)
    {
        this.entitySlug = slug;
        this.tenantSlug = tenantSlug;
    }

    public String getEntitySlug()
    {
        return entitySlug;
    }

    public String getTenantSlug()
    {
        return tenantSlug;
    }

    public static Reference valueOf(String serialized)
    {
        if (serialized.indexOf('@') < 1 || serialized.indexOf('@') >= (serialized.length() - 1)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid reference")
                            .build()
            );
        }

        String entitySlug = StringUtils.substringBefore(serialized, "@");
        String tenantSlug = StringUtils.substringAfter(serialized, "@");

        return new Reference(entitySlug, tenantSlug);
    }
}
