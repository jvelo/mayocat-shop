package org.mayocat.shop.api.v1.resources;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.mayocat.shop.Slugifier;
import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.model.reference.EntityReference;
import org.mayocat.shop.store.AttachmentStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;

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

    protected List<Attachment> getAttachmentList()
    {
        return this.attachmentStore.get().findAll(0, 0);
    }

    protected Response addAttachment(InputStream data, String originalFilename, String title, String description,
            Optional<EntityReference> parent)
    {
        if (data == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("No file were present\n")
                    .type(MediaType.TEXT_PLAIN_TYPE).build();
        }

        Attachment attachment = new Attachment(parent.orNull());

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
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid file name\n")
                    .type(MediaType.TEXT_PLAIN_TYPE).build();
        }

        attachment.setSlug(slug);
        attachment.setData(data);
        attachment.setTitle(title);
        attachment.setDescription(description);

        if (attachment.getReference().getParent() != null) {
            attachment.setParentId(attachmentStore.get().getId(attachment.getReference().getParent()));
        }

        return this.addAttachment(attachment, 0);
    }

    private Response addAttachment(Attachment attachment, int recursionLevel)
    {
        if (recursionLevel > 50) {
            // Defensive stack overflow prevention, even though this should not happen
            return Response.serverError().entity("Failed to create attachment slug").build();
        }
        try {
            try {
                this.attachmentStore.get().create(attachment);
                return Response.noContent().build();
            } catch (InvalidEntityException e) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("Invalid attachment\n")
                        .type(MediaType.TEXT_PLAIN_TYPE).build();
            }
        } catch (EntityAlreadyExistsException e) {
            attachment.setSlug(attachment.getSlug() + RandomStringUtils.randomAlphanumeric(3));
            return this.addAttachment(attachment, recursionLevel + 1);
        }
    }
}
