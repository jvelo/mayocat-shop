/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.resources;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mayocat.Slugifier;
import org.mayocat.model.Attachment;
import org.mayocat.model.AttachmentData;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.InvalidEntityException;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

/**
 * @version $Id$
 */
public class AbstractAttachmentResource
{
    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Slugifier slugifier;

    protected AttachmentStore getAttachmentStore()
    {
        return attachmentStore.get();
    }

    protected Slugifier getSlugifier()
    {
        return this.slugifier;
    }

    protected List<Attachment> getAttachmentList()
    {
        return this.attachmentStore.get().findAll(0, 0);
    }

    protected Attachment addAttachment(InputStream data, String originalFilename, String title, String description,
            Optional<UUID> parent)
    {
        if (data == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("No file were present\n")
                    .type(MediaType.TEXT_PLAIN_TYPE).build());
        }

        Attachment attachment = new Attachment();

        String fileName;
        String extension = null;

        if (originalFilename.indexOf(".") > 0) {
            extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
            attachment.setExtension(extension);
            fileName = StringUtils.removeEnd(originalFilename, "." + extension);
        } else {
            fileName = originalFilename;
        }

        String slug = this.slugifier.slugify(fileName);
        if (Strings.isNullOrEmpty(slug)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid file name\n")
                            .type(MediaType.TEXT_PLAIN_TYPE).build());
        }

        attachment.setSlug(slug);
        attachment.setData(new AttachmentData(data));
        attachment.setTitle(title);
        attachment.setDescription(description);

        if (parent.isPresent()) {
            attachment.setParentId(parent.get());
        }

        return this.addAttachment(attachment, 0);
    }

    private Attachment addAttachment(Attachment attachment, int recursionLevel)
    {
        if (recursionLevel > 50) {
            // Defensive stack overflow prevention, even though this should not happen
            throw new WebApplicationException(
                    Response.serverError().entity("Failed to create attachment slug").build());
        }
        try {
            try {
                return this.attachmentStore.get().create(attachment);
            } catch (InvalidEntityException e) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid attachment\n")
                        .type(MediaType.TEXT_PLAIN_TYPE).build());
            }
        } catch (EntityAlreadyExistsException e) {
            attachment.setSlug(attachment.getSlug() + RandomStringUtils.randomAlphanumeric(3));
            return this.addAttachment(attachment, recursionLevel + 1);
        }
    }
}
