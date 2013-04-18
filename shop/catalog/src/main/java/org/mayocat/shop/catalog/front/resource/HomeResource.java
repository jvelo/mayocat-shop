package org.mayocat.shop.catalog.front.resource;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.Execution;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.rest.Resource;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component(HomeResource.PATH)
@Path(HomeResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class HomeResource extends AbstractFrontResource implements Resource
{
    public static final String PATH = ROOT_PATH;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private CatalogService catalogService;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Execution execution;

    @GET
    public FrontView getHomePage(@Context Breakpoint breakpoint, @Context UriInfo uriInfo)
    {
        FrontView result = new FrontView("home", breakpoint);
        Map<String, Object> context = getContext(uriInfo);

        context.put("template", new HashMap<String, Object>()
        {
            {
                put("home", true);
            }
        });

        result.putContext(context);

        return result;
    }
}
