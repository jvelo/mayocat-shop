package org.mayocat.shop.catalog.front.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.shop.catalog.CatalogService;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Theme;
import org.mayocat.url.EntityURLFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class CollectionContextBuilder implements ContextConstants
{
    public static final String PATH = Resource.ROOT_PATH + CollectionEntity.PATH;

    private Theme theme;

    private ConfigurationService configurationService;

    private AttachmentStore attachmentStore;

    private ThumbnailStore thumbnailStore;

    private ImageContextBuilder imageContextBuilder;

    private EntityURLFactory urlFactory;

    public CollectionContextBuilder(EntityURLFactory urlFactory, ConfigurationService configurationService,
            AttachmentStore attachmentStore, ThumbnailStore thumbnailStore, Theme theme)
    {
        this.urlFactory = urlFactory;
        this.theme = theme;
        this.attachmentStore = attachmentStore;
        this.thumbnailStore = thumbnailStore;
        this.configurationService = configurationService;
        this.imageContextBuilder = new ImageContextBuilder(theme);
    }

    public Map<String, Object> build(final Collection collection, List<Image> images)
    {
        return this.build(collection, images, null);
    }

    public Map<String, Object> build(final Collection collection, List<Image> images, List<Product> products)
    {
        Map<String, Object> collectionContext = Maps.newHashMap();
        collectionContext.put("title", collection.getTitle());
        collectionContext.put("description", collection.getDescription());
        collectionContext.put(SLUG, collection.getSlug());
        collectionContext.put(URL, "/" + urlFactory.create(collection));

        List<Map<String, Object>> productsContext = Lists.newArrayList();

        ProductContextBuilder productContextBuilder =
                new ProductContextBuilder(urlFactory, configurationService, attachmentStore, thumbnailStore, theme);

        if (products != null) {
            for (final Product product : products) {
                List<Attachment> attachments = this.attachmentStore.findAllChildrenOf(product);
                List<Image> productImages = new ArrayList<Image>();
                for (Attachment attachment : attachments) {
                    if (AbstractFrontResource.isImage(attachment)) {
                        List<Thumbnail> thumbnails = thumbnailStore.findAll(attachment);
                        Image image = new Image(attachment, thumbnails);
                        productImages.add(image);
                    }
                }

                Map<String, Object> productContext = productContextBuilder.build(product, productImages);
                productsContext.add(productContext);
            }
            collectionContext.put("products", productsContext);
        }

        Map<String, Object> imagesContext = Maps.newHashMap();
        List<Map<String, String>> allImages = Lists.newArrayList();

        Image featuredImage = null;

        if (images.size() > 0) {
            for (Image image : images) {
                if (featuredImage == null && image.getAttachment().getId().equals(collection.getFeaturedImageId())) {
                    featuredImage = image;
                }
                allImages.add(imageContextBuilder.createImageContext(image, image == featuredImage));
            }
            if (featuredImage == null) {
                // If no featured image has been set, we use the first image in the array.
                featuredImage = images.get(0);
            }
            imagesContext.put("featured", imageContextBuilder.createImageContext(featuredImage, true));
        } else {
            // Create placeholder image
            Map<String, String> placeholder = imageContextBuilder.createPlaceholderImageContext(true);
            imagesContext.put("featured", placeholder);
            allImages = Arrays.asList(placeholder);
        }

        imagesContext.put("all", allImages);
        collectionContext.put("images", imagesContext);

        return collectionContext;
    }
}
