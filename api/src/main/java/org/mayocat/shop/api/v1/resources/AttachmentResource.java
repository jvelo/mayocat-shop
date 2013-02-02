package org.mayocat.shop.api.v1.resources;

import java.io.InputStream;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.shop.Slugifier;
import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.rest.resources.Resource;
import org.mayocat.shop.store.AttachmentStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * @version $Id$
 */
@Component("/api/1.0/attachment")
@Path("/api/1.0/attachment")
@ExistingTenant
public class AttachmentResource extends AbstractAttachmentResource implements Resource
{
    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addImages(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("title") String title)
    {
        return this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, null);
    }
}
