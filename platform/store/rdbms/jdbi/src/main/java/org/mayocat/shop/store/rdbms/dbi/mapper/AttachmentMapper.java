package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.model.reference.EntityReference;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class AttachmentMapper implements ResultSetMapper<Attachment>
{
    @Override
    public Attachment map(int i, ResultSet resultSet, StatementContext statementContext) throws SQLException
    {
        EntityReference reference = new EntityReference("attachment", resultSet.getString("slug"),
                Optional.<EntityReference>absent());
        Attachment attachment = new Attachment(reference);
        attachment.setId(resultSet.getLong("id"));
        attachment.setTitle(resultSet.getString("title"));
        attachment.setSlug(resultSet.getString("slug"));
        attachment.setData(resultSet.getBinaryStream("data"));
        attachment.setExtension(resultSet.getString("extension"));
        return attachment;
    }
}
