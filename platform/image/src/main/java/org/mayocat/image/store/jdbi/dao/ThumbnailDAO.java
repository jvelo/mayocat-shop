package org.mayocat.image.store.jdbi.dao;

import java.util.List;

import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.jdbi.mapper.ThumbnailMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.GetGeneratedKeys;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;

/**
 * @version $Id$
 */
@RegisterMapper(ThumbnailMapper.class)
public interface ThumbnailDAO extends Transactional<ThumbnailDAO>
{
    @GetGeneratedKeys
    @SqlUpdate
    (
        "INSERT INTO thumbnail " +
        "            (attachment_id, " +
        "             source, " +
        "             hint, " +
        "             ratio, " +
        "             x, " +
        "             y," +
        "             width," +
        "             height) " +
        "VALUES      (:thumbnail.attachmentId, " +
        "             :thumbnail.source, " +
        "             :thumbnail.hint, " +
        "             :thumbnail.ratio, " +
        "             :thumbnail.x, " +
        "             :thumbnail.y, " +
        "             :thumbnail.width, " +
        "             :thumbnail.height) "
    )
    Integer createThumbnail(@BindBean("thumbnail") Thumbnail thumbnail);

    @SqlUpdate
    (
        "UPDATE thumbnail " +
        "SET    x = :thumbnail.x, " +
        "       y = :thumbnail.y, " +
        "       width = :thumbnail.width, " +
        "       height = :thumbnail.height " +
        "WHERE  thumbnail.attachment_id = :thumbnail.attachmentId " +
        "       AND thumbnail.source = :thumbnail.source " +
        "       AND thumbnail.hint = :thumbnail.hint " +
        "       AND thumbnail.ratio = :thumbnail.ratio "
    )
    Integer updateThumbnail(@BindBean("thumbnail") Thumbnail thumbnail);

    @SqlQuery(
        "SELECT attachment_id, " +
        "       source, " +
        "       hint, " +
        "       ratio, " +
        "       x, " +
        "       y, " +
        "       width, " +
        "       height " +
        "FROM   thumbnail " +
        "WHERE  attachment_id = :id" +
        "       AND source = :source " +
        "       AND hint = :hint " +
        "       AND ratio = :ratio"
    )
    Thumbnail findThumbnail(@Bind("id") Long id, @Bind("source") String source, @Bind("hint") String hint,
            @Bind("ratio") String ratio);

    @SqlQuery
    (
        "SELECT attachment_id, " +
        "       source, " +
        "       hint, " +
        "       ratio, " +
        "       x, " +
        "       y, " +
        "       width, " +
        "       height " +
        "FROM   thumbnail " +
        "WHERE  attachment_id = :attachment"
    )
    List<Thumbnail> findThumbnails(@Bind("attachment") Long id);
}
