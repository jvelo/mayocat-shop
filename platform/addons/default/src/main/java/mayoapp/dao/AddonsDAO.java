package mayoapp.dao;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.mayocat.addons.binder.BindAddon;
import org.mayocat.addons.mapper.AddonMapper;
import org.mayocat.model.Addon;
import org.mayocat.model.HasAddons;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.unstable.BindIn;

/**
 * @version $Id$
 */
public interface AddonsDAO<T extends HasAddons>
{
    @RegisterMapper(AddonMapper.class)
    @SqlQuery
    (
        "SELECT * FROM addon WHERE entity_id = :entity.id"
    )
    List<Addon> findAddons(@BindBean("entity") T entity);

    @RegisterMapper(AddonMapper.class)
    @SqlQuery
    (
        "SELECT * FROM addon " +
        "WHERE    entity_id in ( <ids> )"
    )
    List<Addon> findAllAddonsForIds(@BindIn("ids") List<UUID> ids);

    @SqlUpdate
    (
        "INSERT INTO addon " +
        "            (entity_id, " +
        "             source, " +
        "             addon_group, " +
        "             addon_key," +
        "             type," +
        "             value) " +
        "VALUES      (:entity.id, " +
        "             :addon.source, " +
        "             :addon.group, " +
        "             :addon.key," +
        "             :addon.type," +
        "             :addon.value) "
    )
    void createAddon(@BindBean("entity") T entity, @BindAddon("addon") Addon addon);

    @SqlUpdate
    (
        "UPDATE addon " +
        "SET type= :addon.type, " +
        "    value = :addon.value " +
        "WHERE entity_id = :entity.id " +
        "AND   source = :addon.source " +
        "AND   addon_key = :addon.key " +
        "AND   addon_group = :addon.group "
    )
    void updateAddon(@BindBean("entity") T entity, @BindAddon("addon")Addon addon);

    @SqlUpdate
    (
        "DELETE FROM addon " +
        "WHERE       addon.entity_id = :entity.id"
    )
    Integer deleteAddons(@BindBean("entity") T entity);
}
