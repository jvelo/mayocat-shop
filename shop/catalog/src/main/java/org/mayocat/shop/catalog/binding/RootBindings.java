package org.mayocat.shop.catalog.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings;
import org.mayocat.shop.catalog.front.builder.ProductBindingBuilder;
import org.mayocat.shop.catalog.meta.CollectionEntity;
import org.mayocat.shop.catalog.model.Collection;
import org.mayocat.context.Execution;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.CollectionStore;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.front.FrontBindingSupplier;
import org.mayocat.shop.front.annotation.Bindings;
import org.mayocat.shop.front.annotation.FrontBinding;
import org.mayocat.shop.front.bindings.BindingsConstants;
import org.mayocat.shop.front.resources.ResourceResource;
import org.mayocat.store.AttachmentStore;
import org.mayocat.theme.Theme;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
@Component("home")
public class RootBindings implements FrontBindingSupplier, BindingsConstants
{
    public final static String SITE = "site";

    public final static String SITE_TITLE = "title";

    public final static String SITE_TAGLINE = "tagline";

    public static final String THEME_PATH = "THEME_PATH";

    @Inject
    private Execution execution;

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

    @FrontBinding(path = "/")
    public void contributeRootBindings(@Bindings Map data)
    {
        final GeneralSettings config = execution.getContext().getSettings(GeneralSettings.class);

        data.put(THEME_PATH, ResourceResource.PATH);

        data.put(SITE, new HashMap()
        {
            {
                put(SITE_TITLE, config.getName().getValue());
                put(SITE_TAGLINE, config.getTagline().getValue());
            }
        });

        // FIXME
        // Do we always want to support the collections binding ?
        // Or should it be supported via the menu builder like the products.
        List<Collection> collections = this.collectionStore.get().findAll(24, 0);
        List<Map<String, Object>> collectionsBinding = Lists.newArrayList();

        for (final Collection collection : collections) {
            collectionsBinding.add(new HashMap<String, Object>()
            {
                {
                    put(BindingsConstants.URL, "/" + CollectionEntity.PATH + "/" + collection.getSlug());
                    put("title", collection.getTitle());
                    put("description", collection.getDescription());
                }
            });
        }

        data.put(COLLECTIONS, collectionsBinding);

        // Put page title and description, mainly for the home page, this will typically get overridden by sub-pages
        data.put(PAGE_TITLE, config.getName().getValue());
        data.put(PAGE_DESCRIPTION, config.getTagline().getValue());

        // FIXME
        // Temporarly put a product list in the context.
        // It is prefixed with __unsupported__ since the goal is to replace this with a "menu builder" feature

        List<Map<String, Object>> productsBinding = Lists.newArrayList();
        List<Product> products = this.productStore.get().findAllOnShelf(24, 0);
        java.util.Collection<Long> featuredImageIds = Collections2.transform(products,
                new Function<Product, Long>()
                {
                    @Override
                    public Long apply(final Product product)
                    {
                        return product.getFeaturedImageId();
                    }
                }
        );
        List<Long> ids = new ArrayList<Long>(Collections2.filter(featuredImageIds, Predicates.notNull()));
        List<Attachment> allImages;
        List<Thumbnail> allThumbnails;
        if (ids.isEmpty()) {
            allImages = Collections.emptyList();
            allThumbnails = Collections.emptyList();
        } else {
            allImages = this.attachmentStore.get().findByIds(ids);
            allThumbnails = this.thumbnailStore.get().findAllForIds(ids);
        }

        final CatalogSettings configuration = configurationService.getSettings(CatalogSettings.class);
        final GeneralSettings generalSettings = configurationService.getSettings(GeneralSettings.class);
        Theme theme = this.execution.getContext().getTheme();
        ProductBindingBuilder builder = new ProductBindingBuilder(configuration, generalSettings, theme);

        for (final Product product : products) {
            java.util.Collection<Attachment> attachments = Collections2.filter(allImages, new Predicate<Attachment>()
            {
                @Override
                public boolean apply(@Nullable Attachment attachment)
                {
                    return attachment.getId().equals(product.getFeaturedImageId());
                }
            });
            List<Image> images = new ArrayList<Image>();
            for (final Attachment attachment : attachments) {
                java.util.Collection<Thumbnail> thumbnails =
                        Collections2.filter(allThumbnails, new Predicate<Thumbnail>()
                        {
                            @Override
                            public boolean apply(@Nullable Thumbnail thumbnail)
                            {
                                return thumbnail.getAttachmentId().equals(attachment.getId());
                            }
                        });
                Image image = new Image(attachment, new ArrayList<Thumbnail>(thumbnails));
                images.add(image);
            }
            Map<String, Object> productContext = builder.build(product, images);
            productsBinding.add(productContext);
        }

        data.put("__unsupported__products", productsBinding);
    }
}
