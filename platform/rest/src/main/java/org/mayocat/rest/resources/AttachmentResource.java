/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.resources;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.model.Attachment;
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
        Attachment created = this.addAttachment(uploadedInputStream, fileDetail.getFileName(), title, description,
                Optional.<UUID>absent());

        try {
            return Response.created(new URI(created.getSlug())).build();
        } catch (URISyntaxException e) {
            logger.error("Failed to created attachment URI", e);
            return Response.serverError().build();
        }
    }

}
