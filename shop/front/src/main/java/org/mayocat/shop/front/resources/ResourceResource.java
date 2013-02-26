package org.mayocat.shop.front.resources;

import java.io.File;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.mayocat.shop.rest.resources.Resource;
import org.xwiki.component.annotation.Component;

import com.google.common.io.Resources;

/**
 * @version $Id$
 */
@Component("/resource/")
@Path("/resource/")
public class ResourceResource implements Resource
{
    @GET
    @Path("{path:.+}")
    public Response getResource(@PathParam("path") String resource) throws Exception
    {
        try {
            URL url = Resources.getResource(contextThemePath() + resource);
            File file = new File(url.toURI());
            String mimeType = getMimetype(file);
            return Response.ok(file, mimeType).build();
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(404);
        }
    }

    private String contextThemePath()
    {
        return "themes/default/";
    }

    private String getMimetype(File file)
    {
        MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
        mtftp.addMimeTypes("text/css css");
        return mtftp.getContentType(file);
    }
}
