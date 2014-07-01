/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.web.delegate

import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.image.store.ThumbnailStore
import org.mayocat.localization.EntityLocalizationService
import org.mayocat.model.Attachment
import org.mayocat.model.Entity
import org.mayocat.model.EntityList
import org.mayocat.attachment.store.AttachmentStore
import org.mayocat.store.EntityListStore

/**
 * Delegate helper for web views of entities with an image gallery.
 *
 * @version $Id$
 */
@CompileStatic
class ImageGalleryWebViewDelegate
{
    AttachmentStore attachmentStore

    ThumbnailStore thumbnailStore

    EntityLocalizationService localizationService

    EntityListStore entityListStore

    List<Image> getImagesForImageGallery(Entity entity, List<Attachment> attachments)
    {
        EntityList galleryImages = entityListStore.findListByHintAndParentId("image_gallery", entity.id)

        def images = [] as List<Image>

        if (galleryImages != null && galleryImages.entities.size() > 0) {
            galleryImages.entities.each({ UUID imageId ->
                Attachment found = attachments.find({ Attachment attachment -> attachment.id == imageId })
                if (found) {
                    def thumbnails = thumbnailStore.findAll(found);
                    images << new Image(localizationService.localize(found) as Attachment, thumbnails)
                }
            })
        }

        images
    }
}
