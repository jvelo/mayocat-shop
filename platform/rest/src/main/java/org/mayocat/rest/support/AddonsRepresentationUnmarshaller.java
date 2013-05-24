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
