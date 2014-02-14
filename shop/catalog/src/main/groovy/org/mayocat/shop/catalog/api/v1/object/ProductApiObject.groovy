package org.mayocat.shop.catalog.api.v1.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import com.google.common.base.Strings
import groovy.transform.CompileStatic
import groovy.transform.TypeChecked
import org.hibernate.validator.constraints.NotEmpty
import org.mayocat.addons.model.AddonField
import org.mayocat.addons.model.BaseProperties
import org.mayocat.addons.util.AddonUtils
import org.mayocat.configuration.PlatformSettings
import org.mayocat.model.Addon
import org.mayocat.shop.catalog.model.Product
import org.mayocat.theme.ThemeDefinition

/**
 * API object for product APIs
 *
 * See {@link org.mayocat.shop.catalog.api.v1.ProductApi}
 *
 * @version $Id$
 */
@CompileStatic
class ProductApiObject extends BaseApiObject
{
    String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String model;

    @NotEmpty
    String title;

    String description;

    Boolean onShelf;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    BigDecimal price;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    BigDecimal weight;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Integer stock;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<AddonApiObject> addons

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _relationships;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<Locale, Map<String, Object>> _localized;

    @JsonIgnore
    def withProduct(Product product)
    {
        this.with {
            slug = product.slug
            title = product.title
            description = product.description
            onShelf = product.onShelf
            price = product.price
            weight = product.weight
            stock = product.stock

            type = product.type.orNull()
            model = product.model.orNull()

            _localized = product.localizedVersions
        }
    }

    @JsonIgnore
    Product toProduct(PlatformSettings platformSettings, Optional<ThemeDefinition> themeDefinition,
            Optional<Product> parent = Optional.absent())
    {
        def product = new Product()
        product.with {
            title = this.title
            description = this.description
            onShelf = this.onShelf
            price = this.price
            weight = this.weight
            stock = this.stock

            setModel this.model
            setType this.type

            setLocalizedVersions this._localized
        }

        if (addons) {
            List<Addon> productAddons = []
            addons.each({ AddonApiObject addon ->
                Addon productAddon = addon.toAddon()
                Optional<AddonField> definition = findAddonDefinition(productAddon, platformSettings, themeDefinition, parent)
                if (definition.isPresent() && !definition.get().properties.containsKey(BaseProperties.READ_ONLY)) {
                    // - Addons for which no definition can be found are ignored
                    // - Addons declared "Read only" are ignored : they can't be updated via this API !
                    productAddons << productAddon
                }
            })

            product.addons = productAddons
        }

        product
    }

    @JsonIgnore
    def withCollectionRelationships(List<org.mayocat.shop.catalog.model.Collection> collections)
    {
        if (_relationships == null) {
            _relationships = [:]
        }

        List collectionRelationships = [];

        collections.each({ org.mayocat.shop.catalog.model.Collection collection ->
            def link = "/api/collections/${collection.slug}"
            collectionRelationships << [
                    title: collection.title,
                    slug: collection.slug,
                    _links: [self: [href: link]],
                    _href: link
            ]
        })

        _relationships.collections = collectionRelationships
    }

    @JsonIgnore
    def withEmbeddedImages(List<ImageApiObject> images)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        ImageApiObject featuredImage

        images.each({ ImageApiObject image ->
            if (image.featured) {
                featuredImage = image
            }
        })

        _embedded.images = images;

        if (featuredImage) {
            _embedded.featuredImage = featuredImage
        }
    }

    @JsonIgnore
    def withEmbeddedVariants(List<Product> variants)
    {
        if (_embedded == null) {
            _embedded = [:]
        }

        List<ProductApiObject> variantApiObjects = []

        variants.each({ Product variant ->
            ProductApiObject object = new ProductApiObject([
                    _href: "/api/products/${this.slug}/variants/${variant.slug}"
            ])
            object.withProduct(variant)
            if (variant.getAddons().isLoaded()) {
                object.withAddons(variant.getAddons().get())
            }
            variantApiObjects << object
        })

        _embedded.variants = variantApiObjects
    }

    @JsonIgnore
    def withAddons(List<Addon> productAddons)
    {
        if (!addons) {
            addons = []
        }

        productAddons.each({ Addon addon ->
            addons << AddonApiObject.forAddon(addon)
        })
    }

    @JsonIgnore
    def Optional<AddonField> findAddonDefinition(Addon addonToFind, PlatformSettings platformSettings,
            Optional<ThemeDefinition> themeDefinition, Optional<Product> parent)
    {
        def option = Optional.absent();
        if (!parent.isPresent()) {
            // 1. Find in platform
            option = AddonUtils.findAddonDefinition(addonToFind, platformSettings.addons);

            if (!option.isPresent() && themeDefinition.isPresent()) {
                // 2. Find in theme
                option = AddonUtils.findAddonDefinition(addonToFind, themeDefinition.get().addons);
            }
        } else {
            def typeAddons = themeDefinition.get().getProductTypes().get(parent.get().type.orNull())?.getVariants()?.getAddons()
            if (typeAddons) {
                option = AddonUtils.findAddonDefinition(addonToFind, typeAddons);
            }
        }

        return option;
    }
}
