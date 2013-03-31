package org.mayocat.shop.catalog.front.resource;

import java.util.ArrayList;
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

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Execution;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.FrontBindingManager;
import org.mayocat.rest.Resource;
import org.mayocat.shop.catalog.front.builder.ProductBindingBuilder;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
@Component(HomeResource.PATH)
@Path(HomeResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
public class HomeResource extends AbstractFrontResource implements Resource
{
    public static final String PATH = ROOT_PATH;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private FrontBindingManager bindingManager;

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

        Map<String, Object> bindings = bindingManager.getBindings(uriInfo.getPathSegments());

        List<Product> productList = catalogService.findAllProducts(20, 0);
        List<Map<String, Object>> productsBinding = Lists.newArrayList();

        final CatalogSettings configuration = (CatalogSettings)
                configurationService.getSettings(CatalogSettings.class);
        final GeneralSettings generalSettings = (GeneralSettings)
                configurationService.getSettings(GeneralSettings.class);

        Theme theme = this.execution.getContext().getTheme();

        ProductBindingBuilder productBindingBuilder = new ProductBindingBuilder(configuration,
                generalSettings, theme);

        for (final Product product : productList) {
            List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(product);
            List<Image> images = new ArrayList<Image>();
            for (Attachment attachment : attachments) {
                if (AbstractFrontResource.isImage(attachment)) {
                    List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                    Image image = new Image(attachment, thumbnails);
                    images.add(image);
                }
            }

            Map<String, Object> productContext = productBindingBuilder.build(product, images);
            productsBinding.add(productContext);
        }

        bindings.put("products", productsBinding);

        result.putBindings(bindings);

        return result;
    }
}
