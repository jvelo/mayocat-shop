package org.mayocat.addons.store.dbi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mayocat.model.Addon;
import org.mayocat.model.HasAddons;
import org.mayocat.model.Identifiable;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import mayoapp.dao.AddonsDAO;

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
        List<Addon> addons = dao.findAllAddonsForIds(new ArrayList(ids));
        Map<UUID, ArrayList<Addon>> addonsForEntity = Maps.newHashMap();
        for (Addon addon : addons) {
            if (!addonsForEntity.containsKey(addon.getEntityId())) {
                addonsForEntity.put(addon.getEntityId(), new ArrayList<Addon>());
            }
            addonsForEntity.get(addon.getEntityId()).add(addon);
        }
        for (T entity : entities) {
            if (addonsForEntity.containsKey(entity.getId())) {
                entity.setAddons(addonsForEntity.get(entity.getId()));
            }
        }
        return entities;
    }

    public static void createOrUpdateAddons(AddonsDAO dao, HasAddons entity)
    {
        if (!entity.getAddons().isLoaded()) {
            return;
        }
        List<Addon> existing = dao.findAddons(entity);
        for (Addon addon : entity.getAddons().get()) {
            Optional<Addon> original = findAddon(existing, addon);
            if (original.isPresent()) {
                dao.updateAddon(entity, addon);
            } else {
                dao.createAddon(entity, addon);
            }
        }
    }

    public static Optional<Addon> findAddon(List<Addon> existing, Addon addon)
    {
        for (Addon a : existing) {
            if (a.getSource().equals(addon.getSource())
                    && a.getKey().equals(addon.getKey()) && a.getGroup().equals(addon.getGroup()))
            {
                return Optional.of(a);
            }
        }
        return Optional.absent();
    }
}
