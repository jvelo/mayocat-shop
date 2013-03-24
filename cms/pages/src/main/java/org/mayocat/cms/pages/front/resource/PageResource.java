package org.mayocat.cms.pages.front.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.rest.Resource;
import org.mayocat.cms.pages.front.builder.PageBindingBuilder;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.context.Execution;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.shop.front.bindings.BindingsContants;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("/page/")
@Path("/page/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class PageResource extends AbstractFrontResource implements Resource
{
    @Inject
    private Provider<PageStore> pageStore;

    @Inject
    private FrontBindingManager bindingManager;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Execution execution;

    @Path("{slug}")
    @GET
    public FrontView getPage(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        final Page page = pageStore.get().findBySlug(slug);
        if (page == null) {
            return new FrontView("404", breakpoint);
        }

        FrontView result = new FrontView("page", page.getModel(), breakpoint);

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        bindings.put(BindingsContants.PAGE_TITLE, page.getTitle());
        bindings.put(BindingsContants.PAGE_DESCRIPTION, page.getContent());

        Theme theme = this.execution.getContext().getTheme();

        List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(page);
        List<Image> images = new ArrayList<Image>();
        for (Attachment attachment : attachments) {
            if (AbstractFrontResource.isImage(attachment)) {
                List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                Image image = new Image(attachment, thumbnails);
                images.add(image);
            }
        }

        PageBindingBuilder builder = new PageBindingBuilder(theme);
        Map<String, Object> productContext = builder.build(page, images);

        bindings.put("page", productContext);
        result.putBindings(bindings);

        return result;
    }
}
