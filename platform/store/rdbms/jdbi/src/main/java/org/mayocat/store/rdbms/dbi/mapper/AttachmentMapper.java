package org.mayocat.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.model.Attachment;
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
        attachment.setId((UUID) resultSet.getObject("id"));
        attachment.setTitle(resultSet.getString("title"));
        attachment.setDescription(resultSet.getString("description"));
        attachment.setSlug(resultSet.getString("slug"));
        attachment.setData(resultSet.getBinaryStream("data"));
        attachment.setExtension(resultSet.getString("extension"));
        attachment.setParentId((UUID) resultSet.getObject("parent_id"));
        return attachment;
    }
}
