package org.mayocat.shop.front.resources;

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

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.ConfigurationSource;
import org.mayocat.configuration.general.GeneralConfiguration;
import org.mayocat.context.Execution;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.configuration.shop.CatalogConfiguration;
import org.mayocat.shop.catalog.model.Category;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.shop.front.bindings.BindingsContants;
import org.mayocat.shop.front.builder.ProductBindingBuilder;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.base.Resource;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component("/category/")
@Path("/category/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CategoryResource extends AbstractFrontResource implements Resource, BindingsContants
{
    @Inject
    private CatalogService catalogService;

    @Inject
    private FrontBindingManager bindingManager;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Execution execution;

    @Path("{slug}")
    @GET
    public FrontView getCategory(@PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        Category category = catalogService.findCategoryBySlug(slug);
        if (category == null) {
            return new FrontView("404", breakpoint);
        }

        FrontView result = new FrontView("category", breakpoint);

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        bindings.put(PAGE_TITLE, category.getTitle());
        bindings.put(PAGE_DESCRIPTION, category.getDescription());

        // Sets the "current" flag on the current category
        try {
            List<Map<String, Object>> categories = (List<Map<String, Object>>) bindings.get(CATEGORIES);
            for (Map<String, Object> c : categories) {
                if (c.containsKey("url") && c.get("url").equals("/category/" + category.getSlug())) {
                    c.put("current", true);
                }
            }
        }
        catch (ClassCastException e) {
            // Ignore
        }

        // TODO Introduce a notion of "Front representation"
        Map<String, Object> categoryContext = Maps.newHashMap();
        categoryContext.put("title", category.getTitle());
        categoryContext.put("description", category.getDescription());

        List<Product> products = catalogService.findProductsForCategory(category);
        List<Map<String, Object>> productsBinding = Lists.newArrayList();

        final CatalogConfiguration configuration = (CatalogConfiguration)
                configurationService.getConfiguration(CatalogConfiguration.class);
        final GeneralConfiguration generalConfiguration = (GeneralConfiguration)
                configurationService.getConfiguration(GeneralConfiguration.class);
        Theme theme = this.execution.getContext().getTheme();

        ProductBindingBuilder productBindingBuilder = new ProductBindingBuilder(configuration,
                generalConfiguration, theme);

        for (final Product product : products) {
            List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(product);
            List<Image> images = new ArrayList<Image>();
            for (Attachment attachment : attachments) {
                if (isImage(attachment)) {
                    List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                    Image image = new Image(attachment, thumbnails);
                    images.add(image);
                }
            }

            Map<String, Object> productContext = productBindingBuilder.build(product, images);
            productsBinding.add(productContext);
        }

        categoryContext.put("products", productsBinding);

        bindings.put("category", categoryContext);

        result.putBindings(bindings);

        return result;
    }
}
