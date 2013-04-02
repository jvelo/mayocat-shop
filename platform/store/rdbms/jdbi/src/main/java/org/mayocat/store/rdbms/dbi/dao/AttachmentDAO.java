package org.mayocat.store.rdbms.dbi.dao;

import java.util.List;

import org.mayocat.model.Attachment;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.mayocat.store.rdbms.dbi.mapper.AttachmentMapper;
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
        "       AND entity.parent_id = :entity.id"
    )
    List<Attachment> findAttachmentsOfEntity(@BindBean("entity") Entity entity);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN attachment" +
        "               ON entity.id = attachment.entity_id " +
        "WHERE  entity.type = 'attachment' " +
        "       AND entity.parent_id (<ids>)"
    )
    List<Attachment> findAttachmentsOfEntities(@BindIn("ids") List<Long> ids);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN attachment" +
        "               ON entity.id = attachment.entity_id " +
        "WHERE  entity.type = 'attachment' " +
        "       AND attachment.extension in (<extensions>)" +
        "       AND entity.parent_id = :entity.id"
    )
    List<Attachment> findAttachmentsOfEntity(@BindBean("entity") Entity entity,
            @BindIn("extensions") List<String> extensions);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN attachment" +
        "               ON entity.id = attachment.entity_id " +
        "WHERE  entity.type = 'attachment' " +
        "       AND attachment.extension in (<extensions>)" +
        "       AND entity.parent_id in (<ids>)"
    )
    List<Attachment> findAttachmentsOfEntities(@BindIn("ids") List<Long> ids,
            @BindIn("extensions") List<String> extensions);

    @SqlQuery
    (
        "SELECT * " +
        "FROM   entity " +
        "       INNER JOIN attachment" +
        "               ON entity.id = attachment.entity_id " +
        "WHERE  entity.type = 'attachment' " +
        "       AND entity.tenant_id = :tenant.id " +
        "       AND entity.slug = :slug"
    )
    Attachment findBySlug(@Bind("slug") String slug,
            @BindBean("tenant") Tenant tenant);

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
