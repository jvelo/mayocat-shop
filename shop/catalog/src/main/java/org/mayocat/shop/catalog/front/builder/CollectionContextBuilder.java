package org.mayocat.shop.catalog.front.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Attachment;
import org.mayocat.rest.Resource;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.front.builder.ImageContextBuilder;
import org.mayocat.shop.front.context.ContextConstants;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.ThemeDefinition;
import org.mayocat.url.EntityURLFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
public class CollectionContextBuilder implements ContextConstants
{
    public static final String PATH = Resource.ROOT_PATH + CollectionEntity.PATH;

    private ImageContextBuilder imageContextBuilder;

    private EntityURLFactory urlFactory;

    public CollectionContextBuilder(EntityURLFactory urlFactory, ThemeDefinition theme)
    {
        this.urlFactory = urlFactory;
        this.imageContextBuilder = new ImageContextBuilder(theme);
    }

    public Map<String, Object> build(final Collection collection, List<Image> images)
    {
        Map<String, Object> collectionContext = Maps.newHashMap();
        collectionContext.put("title", collection.getTitle());
        collectionContext.put("description", collection.getDescription());
        collectionContext.put(SLUG, collection.getSlug());
        collectionContext.put(URL, "/" + urlFactory.create(collection));

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
