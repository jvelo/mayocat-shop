package org.mayocat.addons.front.builder;

import java.util.List;

import org.mayocat.model.Addon;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AddonContextBuilderHelper
{
    public static Optional<Addon> findAddon(String group, String key, List<Addon> addons, String source)
    {
        for (org.mayocat.model.Addon addon : addons) {
            if (addon.getKey().equals(key) && addon.getGroup().equals(group) &&
                    addon.getSource().toJson().equals(source))
            {
                return Optional.of(addon);
            }
        }
        return Optional.absent();
    }

    public static Optional<Addon> findAddon(String group, String key, List<Addon> addons)
    {
        return findAddon(group, key, addons, "theme");
    }
}
