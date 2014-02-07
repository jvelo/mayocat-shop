package org.mayocat.shop.catalog.api.v1

import com.google.common.base.Optional
import com.google.common.base.Strings
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.StringUtils
import org.mayocat.Slugifier
import org.mayocat.model.Attachment
import org.mayocat.store.AttachmentStore
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.InvalidEntityException

import javax.inject.Provider
import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * Helper class API classes can use to delegate attachment related API operations to.
 *
 * @version $Id$
 */
class AttachmentApiDelegate
{
    private Provider<AttachmentStore> attachmentStore;

    private Slugifier slugifier;

    Attachment addAttachment(InputStream data, String originalFilename, String title, String description,
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

        attachment.with {
            setSlug slug
            setData data
            setTitle title
            setDescription description
        };


        if (parent.isPresent()) {
            attachment.setParentId(parent.get());
        }

        return this.addAttachment(attachment, 0);
    }

    Attachment addAttachment(Attachment attachment, int recursionLevel)
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
            attachment.slug = attachment.slug + RandomStringUtils.randomAlphanumeric(3);
            return this.addAttachment(attachment, recursionLevel + 1);
        }
    }
}
