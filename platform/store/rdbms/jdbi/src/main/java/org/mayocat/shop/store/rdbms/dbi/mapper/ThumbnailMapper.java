package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.model.Thumbnail;
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
        thumbnail.setAttachmentId(result.getLong("attachment_id"));
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
