package org.mayocat.shop.catalog.web

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.configuration.ConfigurationService
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.model.Attachment
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.store.CollectionStore
import org.mayocat.shop.catalog.web.object.PaginationWebObject
import org.mayocat.shop.catalog.web.object.ProductListWebObject
import org.mayocat.shop.catalog.web.object.ProductWebObject
import org.mayocat.store.AttachmentStore
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory

import javax.inject.Inject
import javax.inject.Provider

/**
 * FIXME
 * Implement this as a trait if/when traits makes it to groovy.
 *
 * @version $Id$
 */
@CompileStatic
class AbstractProductListWebView extends AbstractWebView
{
    @Inject
    ConfigurationService configurationService

    @Inject
    Provider<AttachmentStore> attachmentStore

    @Inject
    Provider<ThumbnailStore> thumbnailStore

    @Inject
    Provider<CollectionStore> collectionStore

    @Inject
    WebContext context

    @Inject
    EntityURLFactory urlFactory

    @Inject
    ThemeFileResolver themeFileResolver

    @Inject
    EntityLocalizationService entityLocalizationService

    List<ProductWebObject> createProductListContextList(List<Product> products)
    {
        createProductListContextList(products, null)
    }

    List<ProductWebObject> createProductListContextList(List<Product> products,
            org.mayocat.shop.catalog.model.Collection collection)
    {
        List<UUID> featuredImageIds = products.collect({ Product p -> p.featuredImageId })
                                              .findAll({ UUID id -> id != null }) as List<UUID>

        List<Attachment> allImages;
        List<Thumbnail> allThumbnails;

        if (featuredImageIds.isEmpty()) {
            allImages = [];
            allThumbnails = [];
        } else {
            allImages = this.attachmentStore.get().findByIds(featuredImageIds);
            allThumbnails = this.thumbnailStore.get().findAllForIds(featuredImageIds);
        }

        List<ProductWebObject> list = []
        ThemeDefinition theme = this.context.theme?.definition;

        products.each({ Product product ->

            def featuredImage
            def featuredImageAttachment = allImages.find({ Attachment attachment -> attachment.id == product.featuredImageId })
            if (featuredImageAttachment) {
                featuredImage = new Image(featuredImageAttachment, allThumbnails.findAll({
                    Thumbnail thumbnail -> thumbnail.attachmentId == featuredImageAttachment.id
                }) as List<Thumbnail>)
            }

            ProductWebObject productWebObject = new ProductWebObject()
            productWebObject.withProduct(entityLocalizationService.localize(product) as Product, urlFactory, themeFileResolver,
                    configurationService.getSettings(CatalogSettings.class),
                    configurationService.getSettings(GeneralSettings.class), Optional.fromNullable(theme))

            if (collection) {
                productWebObject.withCollection(collection, urlFactory)
            } else if (product.collections.isLoaded() && product.collections.get().size() > 0) {
                productWebObject.withCollection(product.collections.get().get(0), urlFactory)
            }

            if (featuredImage) {
                productWebObject.withImages([featuredImage] as List<Image>, product.featuredImageId,
                        Optional.fromNullable(theme))
            }

            list << productWebObject
        })

        list
    }

    ProductListWebObject createProductListContext(int currentPage, Integer totalPages,
            List<Product> products, Closure<String> urlBuilder)
    {
        PaginationWebObject pagination = new PaginationWebObject()
        pagination.withPages(currentPage, totalPages, urlBuilder)

        new ProductListWebObject([
                pagination: pagination,
                list: createProductListContextList(products, null)
        ])
    }
}
