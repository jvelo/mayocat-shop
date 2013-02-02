package org.mayocat.shop.store.rdbms.dbi.dao;

import java.io.InputStream;

import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.rdbms.dbi.mapper.AttachmentMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@UseStringTemplate3StatementLocator
@RegisterMapper(AttachmentMapper.class)
public abstract class AttachmentDAO implements EntityDAO<Attachment>, Transactional<AttachmentDAO>
{
    @SqlUpdate
    (
        "INSERT INTO attachment " +
        "            (entity_id, " +
        "             extension, " +
        "             title, " +
        "             data) " +
        "VALUES      (:entity, " +
        "             :attachment.extension, " +
        "             :attachment.title, " +
        "             :attachment.data) "
    )
    public abstract void createAttachment(@Bind("entity") Long entityId, @BindBean("attachment") Attachment attachment);

}
