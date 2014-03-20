/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.object

import groovy.transform.CompileStatic
import org.mayocat.model.Addon
import org.mayocat.model.AddonFieldType
import org.mayocat.model.AddonSource

/**
 * API object representing an addon. See {@link Addon)
 *
 * @version $Id$
 */
@CompileStatic
class AddonApiObject
{
    Object value;

    String type;

    String source;

    String group;

    String key;

    static AddonApiObject forAddon(Addon addon)
    {
        new AddonApiObject([
                value: addon.value,
                type: addon.type.toJson(),
                source: addon.source.toJson(),
                group: addon.group,
                key: addon.key
        ])
    }

    Addon toAddon()
    {
        Addon addon = new Addon();
        addon.with {
            setSource AddonSource.fromJson(this.source)
            setType AddonFieldType.fromJson(this.type)
            setValue this.value
            setKey this.key
            setGroup this.group
        }
        addon
    }
}
