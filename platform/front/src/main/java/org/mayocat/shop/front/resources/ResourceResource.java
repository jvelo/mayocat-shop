package org.mayocat.shop.front.resources;

import java.io.File;
import java.net.URI;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.theme.ThemeSettings;
import org.mayocat.rest.Resource;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.ThemeResource;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
@Component("/resource/")
@Path("/resource/")
public class ResourceResource implements Resource
{
    @Inject
    private ThemeManager themeManager;

    @GET
    @Path("{path:.+}")
    public Response getResource(@PathParam("path") String resource, @Context Breakpoint breakpoint) throws Exception
    {
        try {

            ThemeResource themeResource = themeManager.resolveResource(resource, breakpoint);
            if (resource == null) {
                throw new WebApplicationException(404);
            }

            File file;
            URI uri;

            switch (themeResource.getType()) {
                default:
                case FILE:
                    uri = new URI(themeResource.getPath());
                case CLASSPATH_RESOURCE:
                    uri = Resources.getResource(themeResource.getPath()).toURI();
            }

            file = new File(uri);
            String mimeType = getMimetype(file);
            return Response.ok(file, mimeType).build();
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(404);
        }
    }

    private String getMimetype(File file)
    {
        MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
        mtftp.addMimeTypes("text/css css");
        return mtftp.getContentType(file);
    }
}
