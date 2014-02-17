package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.addons.front.builder.AddonContextBuilder
import org.mayocat.addons.model.AddonGroup
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.image.model.Image
import org.mayocat.shop.catalog.configuration.shop.CatalogSettings
import org.mayocat.shop.catalog.model.Feature
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.front.util.ContextUtils
import org.mayocat.theme.FeatureDefinition
import org.mayocat.theme.ThemeDefinition
import org.mayocat.theme.ThemeFileResolver
import org.mayocat.url.EntityURLFactory

/**
 * Web object for a {@link Product} representation
 *
 * @version $Id$
 */
@CompileStatic
class ProductWebObject
{
    String title;

    String description;

    String url;

    String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    EntityModelWebObject model

    String template

    @JsonInclude(JsonInclude.Include.NON_NULL)
    PriceWebObject unitPrice

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type

    EntityImagesWebObject images

    // available -> for sale and in stock
    // not_for_sale
    // out_of_stock
    String availability

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<FeatureWebObject> availableFeatures;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<SelectedFeatureWebObject> selectedFeatures;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    CollectionWebObject featuredCollection

    Boolean hasVariants = false

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map theme_addons

    def withProduct(Product product, EntityURLFactory urlFactory, ThemeFileResolver themeFileResolver,
            CatalogSettings catalogSettings, GeneralSettings generalSettings, Optional<ThemeDefinition> theme)
    {
        title = ContextUtils.safeString(product.title)
        description = ContextUtils.safeHtml(product.description)
        url = urlFactory.create(product).path
        slug = product.slug

        if (product.model.isPresent() && themeFileResolver.resolveModelPath(product.model.get()).isPresent()) {
            model = new EntityModelWebObject([
                    template: themeFileResolver.resolveModelPath(product.model.get()).get(),
                    slug: product.model.get()
            ])
            template = themeFileResolver.resolveModelPath(product.model.get()).get()
        } else {
            template = "product.html"
        }

        // Addons
        if (product.addons.isLoaded() && theme.isPresent()) {
            def addonContextBuilder = new AddonContextBuilder();
            Map<String, AddonGroup> themeAddons = theme.get().getAddons();
            theme_addons = addonContextBuilder.build(themeAddons, product.getAddons().get());
        }

        if (product.type.isPresent()) {
            type = product.type.get()
        }

        if (!product.isVirtual()) {
            // Prices
            if (product.unitPrice != null) {
                def locale = generalSettings.locales.mainLocale.value;
                def currency = catalogSettings.currencies.mainCurrency.value;

                unitPrice = new PriceWebObject()
                unitPrice.withPrice(product.unitPrice, currency, locale)
            }

            def inStock = true
            if (catalogSettings.productsSettings.stock.value) {
                // A stock is managed, check it
                if (product.stock <= 0) {
                    inStock = false
                }
            }

            if (product.unitPrice != null && inStock) {
                availability = "available"
            } else if (product.unitPrice != null) {
                availability = "out_of_stock"
            } else {
                availability = "not_for_sale"
            }
        }
    }

    def withCollection(org.mayocat.shop.catalog.model.Collection collection, EntityURLFactory urlFactory)
    {
        featuredCollection = new CollectionWebObject()
        featuredCollection.withCollection(collection, urlFactory)
    }

    def withFeaturesAndVariants(List<Feature> allFeatures, List<Product> variants, Map<String, String> selectedFeaturesList,
            Optional<ThemeDefinition> theme)
    {
        if (!theme.isPresent() || !type || !theme.get().productTypes.containsKey(type)) {
            // For now only product with a type can have variants
            return false;
        }

        hasVariants = true
        availableFeatures = [];

        if (selectedFeaturesList != null && selectedFeaturesList.size() > 0) {
            selectedFeatures = []
        }

        def types = theme.get().productTypes.get(type);

        // 1. Create the list of selected features

        allFeatures.findAll({ Feature feature ->
            // Filter out features for which we don't have a definition or for which the key is not defined
            types.features.containsKey(feature.feature) && (types.features.get(feature.feature).keys.size() == 0 ||
                    types.features.get(feature.feature).keys.containsKey(feature.featureSlug))

        }).each({ Feature feature ->

            FeatureDefinition definition = types.features.get(feature.feature)

            if (selectedFeaturesList.containsKey(feature.feature)
                    && selectedFeaturesList.get(feature.feature) == feature.featureSlug) {

                // This is the selected feature for this key, we add it to the list of selected features

                selectedFeatures << new SelectedFeatureWebObject([
                        featureName: definition.name,
                        featureSlug: feature.feature,
                        title: feature.title,
                        slug: feature.featureSlug
                ])
            }
        });

        // 2. Create the list of available features

        for (feature in types.features.entrySet()) {
            if (!selectedFeaturesList.containsKey(feature.key)) {
                // Add the feature object
                availableFeatures << new FeatureWebObject([
                        name: feature.value.name,
                        list: allFeatures.findAll({ Feature feat ->
                            feat.feature.equals(feature.key) && variants.any({ Product variant ->
                                // At least one variant must contains this feature
                                variant.features.any({ UUID id ->
                                    feat.id == id
                                })
                            })
                        }).collect({ Feature feat ->
                            new FeatureListItemWebObject([
                                    slug: feat.featureSlug,
                                    title: feat.title,
                                    url: url + (selectedFeaturesList.size() > 0 ? "/" : "")
                                            + selectedFeaturesList.keySet().collect { String key ->
                                        key + "/" + selectedFeaturesList.get(key)
                                    }.join("/") + "/" + feat.feature + "/" + feat.featureSlug
                            ])
                        })
                ])
            }
        }
    }

    def withImages(List<Image> imagesList, UUID featuredImageId, Optional<ThemeDefinition> theme)
    {
        List<ImageWebObject> all = [];
        ImageWebObject featuredImage;

        if (imagesList.size() > 0) {
            for (Image image : imagesList) {
                def featured = image.attachment.id.equals(featuredImageId)
                ImageWebObject imageWebObject = new ImageWebObject();
                imageWebObject.withImage(image, featured, theme)
                if (featuredImage == null && featured) {
                    featuredImage = imageWebObject;
                }
                all << imageWebObject
            }
            if (featuredImage == null) {
                // If no featured image has been set, we use the first image in the array.
                featuredImage = all.get(0)
            }
        } else {
            // Create placeholder image
            featuredImage = new ImageWebObject()
            featuredImage.withPlaceholderImage(true, theme)
            all = [ featuredImage ]
        }
        images = new EntityImagesWebObject([
                all: all,
                featured: featuredImage
        ])
    }
}
