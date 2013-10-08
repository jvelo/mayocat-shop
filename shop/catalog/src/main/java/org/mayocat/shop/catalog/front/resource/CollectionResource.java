package org.mayocat.shop.catalog.front.resource;

import java.util.ArrayList;
import java.util.Collections;
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

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Execution;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.shop.catalog.front.builder.CollectionContextBuilder;
import org.mayocat.shop.catalog.front.builder.ProductContextBuilder;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.Resource;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component(CollectionResource.PATH)
@Path(CollectionResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CollectionResource extends AbstractFrontResource implements Resource, ContextConstants
{
    public static final String PATH = ROOT_PATH + CollectionEntity.PATH;

    @Inject
    private CatalogService catalogService;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Execution execution;

    @Inject
    private EntityURLFactory urlFactory;

    @Path("{slug}")
    @GET
    public FrontView getCollection(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        Collection collection = catalogService.findCollectionBySlug(slug);
        if (collection == null) {
            return new FrontView("404", breakpoint);
        }

        FrontView result = new FrontView("collection", breakpoint);

        Map<String, Object> context = getContext(uriInfo);

        context.put(PAGE_TITLE, collection.getTitle());
        context.put(PAGE_DESCRIPTION, collection.getDescription());

        Theme theme = this.execution.getContext().getTheme();

        List<Product> products = catalogService.findProductsForCollection(collection);

        CollectionContextBuilder builder = new CollectionContextBuilder(
                urlFactory, configurationService, attachmentStore.get(), thumbnailStore.get(), theme);

        // TODO get the collection images
        context.put("collection", builder.build(collection, Collections.<Image>emptyList(), products));

        // Sets the "current" flag on the current collection
        try {
            List<Map<String, Object>> collections = (List<Map<String, Object>>) context.get(COLLECTIONS);
            for (Map<String, Object> c : collections) {
                if (c.containsKey(ContextConstants.URL) &&
                        c.get(ContextConstants.URL).equals(urlFactory.create(collection)))
                {
                    c.put("current", true);
                }
            }
        } catch (ClassCastException e) {
            // Ignore
        }

        result.putContext(context);

        return result;
    }
}
