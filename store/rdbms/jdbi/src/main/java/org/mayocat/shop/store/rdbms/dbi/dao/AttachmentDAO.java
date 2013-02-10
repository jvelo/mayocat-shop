package org.mayocat.shop.store.rdbms.dbi.dao;

import java.io.InputStream;
import java.util.List;

import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.rdbms.dbi.mapper.AttachmentMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@UseStringTemplate3StatementLocator
@RegisterMapper(AttachmentMapper.class)
public interface AttachmentDAO extends EntityDAO<Attachment>, Transactional<AttachmentDAO>
{
    @SqlUpdate
    (
        "INSERT INTO attachment " +
        "            (entity_id, " +
        "             extension, " +
        "             title, " +
        "             description, " +
        "             data) " +
        "VALUES      (:entity, " +
        "             :attachment.extension, " +
        "             :attachment.title, " +
        "             :attachment.description, " +
        "             :data) "
    )
   void createAttachment(@Bind("entity") Long entityId, @BindBean("attachment") Attachment attachment,
            @Bind("data") byte[] data);


    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN attachment" +
        "               ON entity.id = attachment.entity_id " +
        "WHERE  entity.type = 'attachment' " +
        "       AND entity.tenant_id = :tenant.id" +
        "       AND entity.slug = :filename" +
        "       AND attachment.extension = :extension"
   )
   Attachment findByFileNameAndExtension(@Bind("filename") String fileName, @Bind("extension") String extension,
            @BindBean("tenant") Tenant tenant);
}
