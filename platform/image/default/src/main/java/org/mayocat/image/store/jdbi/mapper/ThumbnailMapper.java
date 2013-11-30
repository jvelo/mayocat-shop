/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.image.model.Thumbnail;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class ThumbnailMapper implements ResultSetMapper<Thumbnail>
{
    @Override
    public Thumbnail map(int index, ResultSet result, StatementContext ctx) throws SQLException
    {
        Thumbnail thumbnail = new Thumbnail();
        thumbnail.setAttachmentId((UUID) result.getObject("attachment_id"));
        thumbnail.setHint(result.getString("hint"));
        thumbnail.setSource(result.getString("source"));
        thumbnail.setRatio(result.getString("ratio"));
        thumbnail.setX(result.getInt("x"));
        thumbnail.setY(result.getInt("y"));
        thumbnail.setWidth(result.getInt("width"));
        thumbnail.setHeight(result.getInt("height"));
        return thumbnail;
    }
}
