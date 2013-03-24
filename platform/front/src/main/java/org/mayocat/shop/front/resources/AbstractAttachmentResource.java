package org.mayocat.shop.front.resources;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;

import org.mayocat.model.Attachment;
import org.mayocat.store.AttachmentStore;

/**
 * @version $Id$
 */
public class AbstractAttachmentResource
{
    @Inject
    private Provider<AttachmentStore> attachmentStore;

    public Response downloadFile(String slug, String extension, ServletContext servletContext)
    {
        String fileName = slug + "." + extension;
        Attachment file = this.attachmentStore.get().findBySlugAndExtension(slug, extension);
        if (file == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.ok(file.getData(), servletContext.getMimeType(fileName))
                .header("Content-disposition", "inline; filename*=utf-8''" + fileName)
                .build();
    }

    protected AttachmentStore getAttachmentStore()
    {
        return this.attachmentStore.get();
    }
}
