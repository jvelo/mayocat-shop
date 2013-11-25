package org.mayocat.shop.catalog.front.resource;

import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
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
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.views.FrontView;
import org.mayocat.shop.catalog.front.builder.ProductContextBuilder;
import org.mayocat.shop.catalog.meta.ProductEntity;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.front.builder.PaginationContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Breakpoint;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.math.IntMath;

/**
 * @version $Id$
 */
@Component(ProductResource.PATH)
@Path(ProductResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class ProductResource extends AbstractFrontResource implements Resource, ContextConstants
{
    public static final String PATH = ROOT_PATH + ProductEntity.PATH;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<AttachmentStore> attachmentStore;

    @Inject
    private Provider<ThumbnailStore> thumbnailStore;

    @Inject
    private Provider<CollectionStore> collectionStore;

    @Inject
    private WebContext context;

    @Inject
    private EntityURLFactory urlFactory;

    @Inject
    private EntityLocalizationService entityLocalizationService;

    private static final Function<Product, UUID> PRODUCT_TO_FEATURED_IMAGE_UUID = new Function<Product, UUID>()
    {
        @Override
        public UUID apply(final Product product)
        {
            return product.getFeaturedImageId();
        }
    };

    private Predicate<? super Attachment> belongsToProduct(final Product product)
    {
        return new Predicate<Attachment>()
        {
            public boolean apply(@Nullable Attachment attachment)
            {
                return attachment.getId().equals(product.getFeaturedImageId());
            }
        };
    }

    private Predicate<? super Thumbnail> isThumbnailOfAttachment(final Attachment attachment)
    {
        return new Predicate<Thumbnail>()
        {
            @Override
            public boolean apply(@Nullable Thumbnail thumbnail)
            {
                return thumbnail.getAttachmentId().equals(attachment.getId());
            }
        };
    }

    @GET
    public FrontView getProducts(@QueryParam("page") @DefaultValue("1") Integer page, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        final int currentPage = page < 1 ? 1 : page;
        Integer numberOfProductsPerPage =
                context.getTheme().getDefinition().getPaginationDefinition("products").getItemsPerPage();

        Integer offset = (page - 1) * numberOfProductsPerPage;
        Integer totalCount = this.productStore.get().countAllOnShelf();
        Integer totalPages = IntMath.divide(totalCount, numberOfProductsPerPage, RoundingMode.UP);

        FrontView result = new FrontView("products", Optional.<String>absent(), breakpoint);
        Map<String, Object> context = getContext(uriInfo);
        context.put(ContextConstants.PAGE_TITLE, "All products");

        final Map<String, Object> productsContext = Maps.newHashMap();
        final List<Map<String, Object>> productsListContext = Lists.newArrayList();
        List<Product> products = this.productStore.get().findAllOnShelf(numberOfProductsPerPage, offset);
        java.util.Collection<UUID> featuredImageIds = Collections2.transform(products, PRODUCT_TO_FEATURED_IMAGE_UUID);
        List<UUID> ids = new ArrayList<>(Collections2.filter(featuredImageIds, Predicates.notNull()));
        List<Attachment> allImages;
        List<Thumbnail> allThumbnails;
        if (ids.isEmpty()) {
            allImages = Collections.emptyList();
            allThumbnails = Collections.emptyList();
        } else {
            allImages = this.attachmentStore.get().findByIds(ids);
            allThumbnails = this.thumbnailStore.get().findAllForIds(ids);
        }

        ProductContextBuilder builder = new ProductContextBuilder(urlFactory, configurationService,
                entityLocalizationService, attachmentStore.get(), thumbnailStore.get(),
                this.context.getTheme().getDefinition());

        for (final Product product : products) {
            java.util.Collection<Attachment> attachments = Collections2.filter(allImages, belongsToProduct(product));
            List<Image> images = new ArrayList<>();
            for (final Attachment attachment : attachments) {
                java.util.Collection<Thumbnail> thumbnails =
                        Collections2.filter(allThumbnails, isThumbnailOfAttachment(attachment));
                Image image = new Image(entityLocalizationService.localize(attachment), new ArrayList<>(thumbnails));
                images.add(image);
            }
            List<org.mayocat.shop.catalog.model.Collection> productCollections =
                    collectionStore.get().findAllForProduct(product);
            product.setCollections(productCollections);
            if (productCollections.size() > 0) {
                // Here we take the first collection in the list, but in the future we should have the featured
                // collection as the parent entity of this product
                product.setFeaturedCollection(productCollections.get(0));
            }
            Map<String, Object> productContext = builder.build(entityLocalizationService.localize(product), images);
            productsListContext.add(productContext);
        }

        productsContext.put("list", productsListContext);

        PaginationContextBuilder paginationContextBuilder = new PaginationContextBuilder();
        productsContext.putAll(paginationContextBuilder
                .build(currentPage, totalPages, new PaginationContextBuilder.UrlBuilder()
                {
                    public String build(int page)
                    {
                        return MessageFormat.format("/products/?page={0}", page);
                    }
                }));

        context.put("products", productsContext);
        result.putContext(context);

        return result;
    }

    @Path("{slug}")
    @GET
    public FrontView getProduct(final @PathParam("slug") String slug, @Context Breakpoint breakpoint,
            @Context UriInfo uriInfo)
    {
        final Product product = this.productStore.get().findBySlug(slug);
        if (product == null) {
            return new FrontView("404", breakpoint);
        }

        List<org.mayocat.shop.catalog.model.Collection> collections =
                collectionStore.get().findAllForProduct(product);
        product.setCollections(collections);
        if (collections.size() > 0) {
            // Here we take the first collection in the list, but in the future we should have the featured
            // collection as the parent entity of this product
            product.setFeaturedCollection(collections.get(0));
        }

        FrontView result = new FrontView("product", product.getModel(), breakpoint);

        Map<String, Object> context = getContext(uriInfo);

        context.put(ContextConstants.PAGE_TITLE, product.getTitle());
        context.put(ContextConstants.PAGE_DESCRIPTION, product.getDescription());

        ThemeDefinition theme = this.context.getTheme().getDefinition();

        List<Attachment> attachments = this.attachmentStore.get().findAllChildrenOf(product);
        List<Image> images = new ArrayList<Image>();
        for (Attachment attachment : attachments) {
            if (isImage(attachment)) {
                List<Thumbnail> thumbnails = thumbnailStore.get().findAll(attachment);
                Image image = new Image(entityLocalizationService.localize(attachment), thumbnails);
                images.add(image);
            }
        }

        ProductContextBuilder builder = new ProductContextBuilder(urlFactory, configurationService,
                entityLocalizationService, attachmentStore.get(), thumbnailStore.get(), theme);
        Map<String, Object> productContext = builder.build(entityLocalizationService.localize(product), images);

        context.put("product", productContext);
        result.putContext(context);

        return result;
    }
}
