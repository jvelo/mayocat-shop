/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration;

import javax.validation.Valid;

import org.mayocat.accounts.model.Role;

import com.fasterxml.jackson.annotation.JsonProperty;

public class MultitenancySettings
{
    @Valid
    @JsonProperty
    private boolean activated = false;

    @Valid
    @JsonProperty
    private String defaultTenant = "default";

    @Valid
    @JsonProperty
    private String resolver = "defaultHostAndSubdomain";

    @Valid
    @JsonProperty
    private Role requiredRoleForTenantCreation = Role.NONE;

    public boolean isActivated()
    {
        return activated;
    }

    public String getDefaultTenantSlug()
    {
        return defaultTenant;
    }

    public String getResolver()
    {
        return resolver;
    }

    public Role getRequiredRoleForTenantCreation()
    {
        return requiredRoleForTenantCreation;
    }
}
