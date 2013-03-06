package org.mayocat.shop.api.v1.resources;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.base.Resource;
import org.mayocat.image.ImageService;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.image.util.ImageUtils;
import org.mayocat.model.Attachment;
import org.mayocat.shop.api.v1.parameters.ImageOptions;
import org.mayocat.shop.api.v1.representations.ThumbnailRepresentation;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
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
