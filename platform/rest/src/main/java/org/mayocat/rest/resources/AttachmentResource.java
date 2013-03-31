package org.mayocat.rest.resources;

import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.rest.Resource;
import org.mayocat.image.ImageService;
import org.mayocat.rest.annotation.ExistingTenant;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;

/**
 * @version $Id$
 */
@Component(AttachmentResource.PATH)
@Path(AttachmentResource.PATH)
@ExistingTenant
public class AttachmentResource extends AbstractAttachmentResource implements Resource
{
    // TODO: Create a module with an attachment entity
    public static final String PATH = API_ROOT_PATH + "attachments";

    @Inject
    private ImageService imageService;

    @Inject
    private Logger logger;

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response addAttachment(@FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @FormDataParam("title") String title, @FormDataParam("description") String description)
    {
        return this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.<Long>absent());
    }

}
