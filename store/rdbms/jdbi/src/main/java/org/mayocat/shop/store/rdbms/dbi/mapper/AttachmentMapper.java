package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.model.Attachment;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class AttachmentMapper implements ResultSetMapper<Attachment>
{
    @Override
    public Attachment map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException
    {
        Attachment attachment = new Attachment();
        attachment.setId(resultSet.getLong("id"));
        attachment.setTitle(resultSet.getString("title"));
        attachment.setSlug(resultSet.getString("slug"));
        attachment.setData(resultSet.getBinaryStream("data"));
        return attachment;
    }
}
