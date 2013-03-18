package org.mayocat.store.rdbms.jdbi;

import java.util.List;

import org.mayocat.model.Addon;
import org.mayocat.model.HasAddons;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AddonsHelper
{
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

    private static Optional<Addon> findAddon(List<Addon> existing, Addon addon)
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
