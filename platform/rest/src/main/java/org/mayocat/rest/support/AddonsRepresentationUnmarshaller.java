/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.support;

import java.util.List;

import org.mayocat.addons.api.representation.AddonRepresentation;
import org.mayocat.model.Addon;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface AddonsRepresentationUnmarshaller
{
    List<Addon> unmarshall(List<AddonRepresentation> addons, boolean includeReadOnlyAddons,
            boolean includeAddonsWithoutDefinition);

    List<Addon> unmarshall(List<AddonRepresentation> addons, boolean includeReadOnlyAddons);

    List<Addon> unmarshall(List<AddonRepresentation> addons);
}
