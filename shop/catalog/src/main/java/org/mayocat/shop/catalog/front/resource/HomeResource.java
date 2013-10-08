package org.mayocat.shop.catalog.front.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import org.mayocat.cms.pages.front.builder.PageContextBuilder;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.Execution;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.mayocat.url.EntityURLFactory;
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
    private Provider<PageStore> pageStore;

    @Inject
    private Execution execution;

    @Inject
    private EntityURLFactory urlFactory;

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

        final Page page = pageStore.get().findBySlug("home");
        if (page != null) {
            context.put(ContextConstants.PAGE_TITLE, page.getTitle());
            context.put(ContextConstants.PAGE_DESCRIPTION, page.getContent());

            Theme theme = this.execution.getContext().getTheme();

            List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(page, Arrays
                    .asList("png", "jpg", "jpeg", "gif"));
            List<Image> images = new ArrayList<>();
            for (Attachment attachment : attachments) {
                if (AbstractFrontResource.isImage(attachment)) {
                    List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                    Image image = new Image(attachment, thumbnails);
                    images.add(image);
                }
            }

            PageContextBuilder builder = new PageContextBuilder(urlFactory, theme);
            Map<String, Object> pageContext = builder.build(page, images);
            context.put("home", pageContext);
        }
        result.putContext(context);
        return result;
    }
}
