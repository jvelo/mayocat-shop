package org.mayocat.shop.catalog.api.v1.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import groovy.transform.TypeChecked
import org.mayocat.image.model.Image

/**
 * Represents a feature API object.
 *
 * @version $Id$
 */
@TypeChecked
class ImageApiObject extends BaseApiObject
{
    String title

    String description

    String slug

    Boolean featured

    FileApiObject file

    @JsonIgnore
    def withImage(Image image)
    {
        def link = "/api/images/${image.attachment.slug}"
        def fileLink = "/images/${image.attachment.slug}.${image.attachment.extension}"
        this.with {
            _href = link
            slug = image.attachment.slug
            title = image.attachment.title
            description = image.attachment.description
            file = new FileApiObject([
                    _href: fileLink,
                    fileName: "${image.attachment.slug}.${image.attachment.extension}",
                    extension: image.attachment.extension
            ])
        }
    }
}
