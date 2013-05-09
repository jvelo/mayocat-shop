package mayoapp.dao;

import java.util.List;

import org.mayocat.model.Entity;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.customizers.Define;

/**
 * @version $Id$
 */
public interface PositionedDAO<E extends Entity>
{
    @SqlBatch
    (
            "UPDATE <type> " +
            "SET    position = :position " +
            "WHERE  entity_id = :entity.id "
    )
    void updatePositions(@Define("type") String type, @BindBean("entity") List<E> entity,
            @Bind("position") List<Integer> position);
}