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

import org.mayocat.accounts.model.Tenant;
import org.mayocat.attachment.model.Attachment;
import org.mayocat.attachment.model.LoadedAttachment;
import org.mayocat.attachment.store.jdbi.mapper.LoadedAttachmentMapper;
import org.mayocat.model.Entity;
import org.mayocat.store.rdbms.dbi.argument.MapAsJsonArgumentFactory;
import org.mayocat.attachment.store.jdbi.mapper.AttachmentMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;
import org.skife.jdbi.v2.unstable.BindIn;

/**
 * @version $Id$
 */
@UseStringTemplate3StatementLocator
@RegisterMapper({ AttachmentMapper.class, LoadedAttachmentMapper.class })
@RegisterArgumentFactory({ MapAsJsonArgumentFactory.class })
public interface AttachmentDAO extends EntityDAO<Attachment>, Transactional<AttachmentDAO>, LocalizationDAO<Attachment>
{
    @SqlUpdate
    void createAttachment(@Bind("entity") UUID entityId, @BindBean("attachment") Attachment attachment,
            @Bind("data") byte[] data);

    @SqlUpdate
    Integer updateAttachment(@BindBean("attachment") Attachment attachment);

    @SqlQuery
    List<Attachment> findAttachmentsOfEntity(@BindBean("entity") Entity entity);

    @SqlQuery
    List<Attachment> findAttachmentsOfEntities(@BindIn("ids") List<UUID> ids);

    @SqlQuery
    List<Attachment> findAttachmentsByIds(@BindIn("ids") List<UUID> ids);

    @SqlQuery
    List<Attachment> findAttachmentsOfEntityWithExtensions(@BindBean("entity") Entity entity,
            @BindIn("extensions") List<String> extensions);

    @SqlQuery
    List<Attachment> findAttachmentsOfEntitiesWithExtensions(@BindIn("ids") List<UUID> ids,
            @BindIn("extensions") List<String> extensions);

    @SqlQuery
    LoadedAttachment findAndLoadById(@Bind("id") UUID id);

    @SqlQuery
    Attachment findById(@Bind("id") UUID id);

    @SqlQuery
    LoadedAttachment findBySlug(@Bind("slug") String slug, @Bind("tenantId") UUID tenantId);

    @SqlQuery
    LoadedAttachment findAndLoadByFileNameAndExtension(@Bind("filename") String fileName, @Bind("extension") String extension,
            @Bind("tenantId") UUID tenantId);

    @SqlQuery
    LoadedAttachment findAndLoadByTenantAndFileNameAndExtension(@Bind("tenant") String tenantSlug,
            @Bind("filename") String fileName, @Bind("extension") String extension);

    @SqlQuery
    Attachment findByFileNameAndExtension(@Bind("filename") String fileName, @Bind("extension") String extension,
            @Bind("tenantId") UUID tenantId);

    @SqlQuery
    Attachment findByTenantAndFileNameAndExtension(@Bind("tenant") String tenantSlug,
            @Bind("filename") String fileName, @Bind("extension") String extension);
}
