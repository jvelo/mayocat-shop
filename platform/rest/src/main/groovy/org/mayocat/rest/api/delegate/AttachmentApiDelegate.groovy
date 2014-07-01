/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.delegate

import com.google.common.base.Optional
import com.google.common.base.Strings
import com.sun.jersey.core.header.FormDataContentDisposition
import com.sun.jersey.multipart.FormDataParam
import groovy.transform.CompileStatic
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.mayocat.Slugifier
import org.mayocat.attachment.MetadataExtractor
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.model.Attachment
import org.mayocat.model.AttachmentData
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.InvalidEntityException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.inject.Provider
import javax.ws.rs.Consumes
import javax.ws.rs.POST
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.Context
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response
import javax.ws.rs.core.UriInfo

/**
 * Helper class API classes can use to delegate attachment related API operations to.
 *
 * @version $Id$
 */
@CompileStatic
class AttachmentApiDelegate
{
    private Map<String, MetadataExtractor> extractors

    private Provider<AttachmentStore> attachmentStore

    private Slugifier slugifier

    private EntityApiDelegateHandler handler

    private Closure doAfterAttachmentAdded

    private Logger logger = LoggerFactory.getLogger(AttachmentApiDelegate.class);

    @Path("{slug}/attachments")
    @Authorized
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    def addAttachment(@PathParam("slug") String slug,
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("filename") String sentFilename,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description,
            @FormDataParam("target") String target,
            @Context UriInfo info)
    {
        def entity = handler.getEntity(slug)
        if (entity == null) {
            return Response.status(404).build()
        }

        def filename = StringUtils.defaultIfBlank(fileDetail.fileName, sentFilename) as String
        def created = this.addAttachment(uploadedInputStream, filename, title, description,
                Optional.of(entity.id))

        if (target && created && doAfterAttachmentAdded) {
            doAfterAttachmentAdded.call(target, entity, filename, created)
        }

        if (created) {
            try {
                // User base URI builder instead of new URI(...) to circumvent Jersey bug
                // See http://stackoverflow.com/a/13704308/1281372
                URI absoluteURI=info.getBaseUriBuilder().path("/images/" + created.getFilename()).build();

                // TODO: not all attachments are images...

                return Response.created(absoluteURI).build();
            } catch (URISyntaxException e) {
                logger.error("Failed to created attachment URI", e);
            }
        }
        return Response.serverError().build();
    }

    Attachment addAttachment(InputStream data, String originalFilename, String title, String description,
            Optional<UUID> parent)
    {
        if (data == null) {
            throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                    .entity("No file were present\n")
                    .type(MediaType.TEXT_PLAIN_TYPE).build())
        }

        Attachment attachment = new Attachment()

        String fileName

        if (originalFilename.indexOf(".") > 0) {
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase()
            attachment.setExtension(extension)
            fileName = StringUtils.removeEnd(originalFilename, "." + extension)
        } else {
            fileName = originalFilename
        }

        String slug = this.slugifier.slugify(fileName)
        if (Strings.isNullOrEmpty(slug)) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST)
                            .entity("Invalid file name\n")
                            .type(MediaType.TEXT_PLAIN_TYPE).build())
        }

        attachment.with {
            setSlug slug
            setData new AttachmentData(data)
            setTitle title
            setDescription description
        }

        if (parent.isPresent()) {
            attachment.setParentId(parent.get())
        }

        Attachment created = this.addAttachment(attachment, 0)

        Map<String, Map<String, Object>> metadata = [:]

        extractors.keySet().each ({ String name ->
            def extractor = extractors.get(name)
            // We retrieved the attachment again from DB to have a stream that is not consumed already
            // (potentially each metadata extractor can consume the stream - and they probably will)
            Attachment retrieved = this.attachmentStore.get().findById(created.id)
            Optional<Map<String, Object>> result = extractor.extractMetadata(retrieved)
            if (result.isPresent()) {
                metadata.put(name, result.get())
            }
        })

        if (!metadata.isEmpty()) {
            created.setMetadata(metadata)
            this.attachmentStore.get().update(created)
        }

        created
    }

    Attachment addAttachment(Attachment attachment, int recursionLevel)
    {
        if (recursionLevel > 50) {
            // Defensive stack overflow prevention, even though this should not happen
            throw new WebApplicationException(
                    Response.serverError().entity("Failed to create attachment slug").build())
        }
        try {
            try {
                return this.attachmentStore.get().create(attachment)
            } catch (InvalidEntityException e) {
                e.printStackTrace()
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid attachment\n")
                        .type(MediaType.TEXT_PLAIN_TYPE).build())
            }
        } catch (EntityAlreadyExistsException e) {
            attachment.slug = attachment.slug + RandomStringUtils.randomAlphanumeric(3)
            return this.addAttachment(attachment, recursionLevel + 1)
        }
    }
}
