/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.jdbi.mapper.ThumbnailMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

/**
 * @version $Id$
 */
@RegisterMapper(ThumbnailMapper.class)
@UseStringTemplate3StatementLocator
public interface ThumbnailDAO extends Transactional<ThumbnailDAO>
{
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
    void createThumbnail(@BindBean("thumbnail") Thumbnail thumbnail);

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
    Thumbnail findThumbnail(@Bind("id") UUID id, @Bind("source") String source, @Bind("hint") String hint,
            @Bind("ratio") String ratio);

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
                    "       AND hint = :hint"
    )
    Thumbnail findThumbnail(@Bind("id") UUID id, @Bind("source") String source, @Bind("hint") String hint);

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
    List<Thumbnail> findThumbnails(@Bind("attachment") UUID id);

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
        "WHERE  attachment_id in ( <ids> )"
    )
    List<Thumbnail> findAllThumbnails(@BindIn("ids") List<UUID> ids);

    @SqlUpdate
    (
        "DELETE FROM thumbnail " +
        "WHERE       attachment_id = :attachment"
    )
    Integer deleteThumbnails(@Bind("attachment") UUID id);
}
