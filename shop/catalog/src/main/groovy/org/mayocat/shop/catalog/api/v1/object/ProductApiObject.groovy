package org.mayocat.shop.catalog.api.v1.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.TypeChecked
import org.hibernate.validator.constraints.NotEmpty
import org.mayocat.image.model.Image
import org.mayocat.shop.catalog.model.Product

/**
 * API object for product APIs
 *
 * See:
 * <ul>
 * <li><code>/api/products/{slug}</code>
 * {@link org.mayocat.shop.catalog.api.v1.ProductApi#getProductV2(java.lang.String)}</li>
 * </ul>
 *
 * @version $Id$
 */
@TypeChecked
class ProductApiObject extends BaseApiObject
{
    String slug;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String model;

    @NotEmpty
    String title;

    String description;

    Boolean onShelf;

    BigDecimal price;

    BigDecimal weight;

    Integer stock;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _embedded

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, Object> _relationships;

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
        }
    }

    @JsonIgnore
    Product toProduct()
    {
        def product = new Product()
        product.with {
            slug = this.slug
            title = this.title
            description = this.description
            onShelf = this.onShelf
            price = this.price
            weight = this.weight
            stock = this.stock
            type = this.type
            model = this.model
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
}
