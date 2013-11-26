package org.mayocat.shop.catalog.front.resource;

import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.context.WebContext;
import org.mayocat.image.model.Image;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.front.builder.CollectionContextBuilder;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.front.builder.PaginationContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.Resource;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.math.IntMath;

/**
 * @version $Id$
 */
@Component(CollectionResource.PATH)
@Path(CollectionResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CollectionResource extends AbstractProductListFrontResource implements Resource, ContextConstants
{
    public static final String PATH = ROOT_PATH + CollectionEntity.PATH;

    @Inject
    private CatalogService catalogService;

    @Inject
    private Provider<ProductStore> productStore;

    @Path("{slug}")
    @GET
    public FrontView getCollection(@PathParam("slug") String slug, @QueryParam("page") @DefaultValue("1") Integer page,
            @Context Breakpoint breakpoint, @Context UriInfo uriInfo)
    {
        final int currentPage = page < 1 ? 1 : page;

        final Collection collection = catalogService.findCollectionBySlug(slug);
        if (collection == null) {
            return new FrontView("404", breakpoint);
        }

        FrontView result = new FrontView("collection", breakpoint);

        Map<String, Object> context = getContext(uriInfo);

        context.put(PAGE_TITLE, collection.getTitle());
        context.put(PAGE_DESCRIPTION, collection.getDescription());

        ThemeDefinition theme = this.context.getTheme().getDefinition();

        Integer numberOfProductsPerPage =
                this.context.getTheme().getDefinition().getPaginationDefinition("collection").getItemsPerPage();

        Integer offset = (page - 1) * numberOfProductsPerPage;
        Integer totalCount = this.productStore.get().countAllForCollection(collection);
        Integer totalPages = IntMath.divide(totalCount, numberOfProductsPerPage, RoundingMode.UP);

        List<Product> products = productStore.get().findForCollection(collection, numberOfProductsPerPage, offset);

        CollectionContextBuilder builder = new CollectionContextBuilder(urlFactory, theme);
        Map<String, Object> collectionContext =
                builder.build(entityLocalizationService.localize(collection), Collections.<Image>emptyList());

        collectionContext.put("products", createProductListContext(currentPage, totalPages, products,
                new PaginationContextBuilder.UrlBuilder()
                {
                    public String build(int page)
                    {
                        return MessageFormat.format("/collections/{0}/?page={1}", collection.getSlug(), page);
                    }
                }));

        // TODO get the collection images
        context.put("collection", collectionContext);

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
