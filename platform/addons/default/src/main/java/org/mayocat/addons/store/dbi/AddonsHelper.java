/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.store.dbi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mayocat.model.AddonGroup;
import org.mayocat.model.HasAddons;
import org.mayocat.model.Identifiable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import mayoapp.dao.AddonsDAO;

import static org.mayocat.addons.util.AddonUtils.asMap;

/**
 * @version $Id$
 */
public class AddonsHelper
{
    public static <T extends Identifiable & HasAddons> List<T> withAddons(List<T> entities, AddonsDAO dao)
    {
        Collection<UUID> ids = Collections2.transform(entities,
                new Function<T, UUID>()
                {
                    @Override
                    public UUID apply(final T entity)
                    {
                        return entity.getId();
                    }
                }
        );
        if (ids.size() <= 0) {
            return entities;
        }
        List<AddonGroup> addons = dao.findAllAddonsForIds(new ArrayList(ids));
        Map<UUID, List<AddonGroup>> addonsForEntity = Maps.newHashMap();
        for (AddonGroup addon : addons) {
            if (!addonsForEntity.containsKey(addon.getEntityId())) {
                addonsForEntity.put(addon.getEntityId(), new ArrayList<AddonGroup>());
            }
            addonsForEntity.get(addon.getEntityId()).add(addon);
        }
        for (T entity : entities) {
            if (addonsForEntity.containsKey(entity.getId())) {
                entity.setAddons(asMap(addonsForEntity.get(entity.getId())));
            }
        }
        return entities;
    }

    public static void createOrUpdateAddons(AddonsDAO dao, HasAddons entity)
    {
        if (!entity.getAddons().isLoaded()) {
            return;
        }
        Map<String, AddonGroup> existing = asMap(dao.findAddons(entity));

        for (String group : entity.getAddons().get().keySet()) {
            AddonGroup addonGroup = entity.getAddons().get().get(group);
            if (existing.containsKey(group)) {
                dao.updateAddonGroup(entity, addonGroup);
            } else {
                dao.createAddonGroup(entity, addonGroup);
            }
        }
    }
}
