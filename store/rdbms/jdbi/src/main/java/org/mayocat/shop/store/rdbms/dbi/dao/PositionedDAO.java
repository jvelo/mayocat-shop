package org.mayocat.shop.store.rdbms.dbi.dao;

import java.util.List;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.store.HasOrderedCollections;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlBatch;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.customizers.Define;

import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;

/**
 * @version $Id$
 */
public interface PositionedDAO<E extends Entity>
{
    @SqlBatch
    (
            "UPDATE <type> " +
            "SET    <type>.position = :position " +
            "WHERE  entity_id = :entity.id "
    )
    void updatePositions(@Define("type") String type, @BindBean("entity") List<E> entity,
            @Bind("position") List<Integer> position);
}