package org.mayocat.shop.front.resources;

import java.io.File;
import java.net.URI;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.mayocat.rest.Resource;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.ThemeResource;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.io.Resources;
import com.sun.org.apache.xpath.internal.res.XPATHErrorResources;

/**
 * @version $Id$
 */
@Component(ResourceResource.PATH)
@Path(ResourceResource.PATH)
public class ResourceResource implements Resource
{
    public static final String PATH = "/resources/";

    @Inject
    private ThemeManager themeManager;

    @Inject
    private Logger logger;

    @GET
    @Path("{path:.+}")
    public Response getResource(@PathParam("path") String resource, @Context Breakpoint breakpoint) throws Exception
    {
        ThemeResource themeResource = themeManager.resolveResource(resource, breakpoint);
        if (resource == null) {
            logger.debug("Resource [{}] with breakpoint [{}] not found", resource, breakpoint);
            throw new WebApplicationException(404);
        }

        File file;

        switch (themeResource.getType()) {
            default:
            case FILE:
                file = new File(themeResource.getPath());
                break;
            case CLASSPATH_RESOURCE:
                URI uri = Resources.getResource(themeResource.getPath()).toURI();
                file = new File(uri);
                break;
        }

        String mimeType = getMimetype(file);
        return Response.ok(file, mimeType).build();
    }

    private String getMimetype(File file)
    {
        MimetypesFileTypeMap mtftp = new MimetypesFileTypeMap();
        mtftp.addMimeTypes("text/css css");
        return mtftp.getContentType(file);
    }
}
