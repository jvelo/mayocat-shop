package org.mayocat.shop.front.resources;

import com.google.common.base.Optional;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.yammer.dropwizard.assets.ResourceURL;
import org.mayocat.rest.Resource;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeManager;
import org.mayocat.theme.ThemeResource;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Date;

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
    public Response getResource(@PathParam("path") String resource, @Context Breakpoint breakpoint, @Context
    Request request) throws Exception
    {
        ThemeResource themeResource = themeManager.getResource(resource, breakpoint);
        if (themeResource == null) {
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

                if (uri.getScheme().equals("jar")) {
                    // Not supported for now
                   return Response.status(Response.Status.NOT_FOUND).build();
                }

                file = new File(uri);
                break;
        }

        String tag = Files.hash(file, Hashing.murmur3_128()).toString();
        EntityTag eTag = new EntityTag(tag);

        URL url = file.toURI().toURL();
        long lastModified = ResourceURL.getLastModified(url);
        if (lastModified < 1) {
            // Something went wrong trying to get the last modified time: just use the current time
            lastModified = System.currentTimeMillis();
        }
        // zero out the millis since the date we get back from If-Modified-Since will not have them
        lastModified = (lastModified / 1000) * 1000;

        CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge(24 * 3600);
        Response.ResponseBuilder builder = request.evaluatePreconditions(new Date(lastModified), eTag);
        String mimeType = guessMimeType(file).or("application/octet-stream");

        if (builder == null) {
            builder = Response.ok(file, mimeType);
        }

        return builder.cacheControl(cacheControl).lastModified(new Date(lastModified)).build();
    }

    private Optional<String> guessMimeType(File file) {
        try {
            return Optional.fromNullable(java.nio.file.Files.probeContentType(file.toPath()));
        } catch (IOException e) {
            this.logger.warn("Error while attempting to resource mime type", e);
            return Optional.absent();
        }
    }
}
