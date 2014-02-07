package org.mayocat.shop.catalog.api.v1.object

import org.mayocat.model.Addon
import org.mayocat.model.AddonFieldType
import org.mayocat.model.AddonSource

/**
 * Doc goes here.
 *
 * @version $Id$
 */
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
