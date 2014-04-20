/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.object

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail

/**
 * Represents a feature API object.
 *
 * @version $Id$
 */
@CompileStatic
class ImageApiObject extends BaseApiObject
{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String title

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String description

    String slug

    Boolean featured

    FileApiObject file

    List<ImageThumbnailApiObject> thumbnails = [];

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<Locale, Map<String, Object>> _localized;

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

            _localized = image.attachment.localizedVersions
        }

        image.thumbnails.each({ Thumbnail thumbnail ->
            thumbnails << new ImageThumbnailApiObject([
                    source: thumbnail.source,
                    hint: thumbnail.hint,
                    ratio: thumbnail.ratio,
                    x: thumbnail.x,
                    y: thumbnail.y,
                    width: thumbnail.width,
                    height: thumbnail.height
            ])
        })
    }
}
