/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
