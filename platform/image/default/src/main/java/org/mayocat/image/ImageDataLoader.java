/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.mayocat.entity.DataLoaderAssistant;
import org.mayocat.entity.EntityData;
import org.mayocat.entity.LoadingOption;
import org.mayocat.entity.StandardOptions;
import org.mayocat.image.model.Image;
import org.mayocat.image.model.ImageGallery;
import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.localization.EntityLocalizationService;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.model.EntityList;
import org.mayocat.store.EntityListStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import static org.mayocat.entity.EntityUtils.asSet;

/**
 * @version $Id$
 */
@Component("images")
public class ImageDataLoader implements DataLoaderAssistant
{
    @Inject
    private EntityListStore entityListStore;

    @Inject
    private ThumbnailStore thumbnailStore;

    @Inject
    private EntityLocalizationService localizationService;

    public <E extends Entity> void load(EntityData<E> entityData, LoadingOption... optionsArray)
    {
        final Set<LoadingOption> options = asSet(optionsArray);

        List<Attachment> attachments = entityData.getChildren(Attachment.class);

        if (attachments.size() == 0) {
            // No need to continue
            return;
        }

        final EntityList list =
                entityListStore.findListByHintAndParentId("image_gallery", entityData.getEntity().getId());

        List<Image> images = FluentIterable.from(attachments).transform(new Function<Attachment, Image>()
        {
            public Image apply(Attachment attachment)
            {
                // Costy :/
                // See this.thumbnailStoreProvider.get().findAllForIds(featuredImageIds);
                List<Thumbnail> thumbnails = thumbnailStore.findAll(attachment);

                return new Image(options.contains(StandardOptions.LOCALIZE) ? localizationService.localize(attachment) :
                        attachment, thumbnails);
            }
        }).toList();

        entityData.setDataList(Image.class, images);

        if (list != null) {
            List<Image> galleryImages = FluentIterable.from(images).filter(new Predicate<Image>()
            {
                public boolean apply(Image input)
                {
                    return list.getEntities().contains(input.getAttachment().getId());
                }
            }).toList();

            ImageGallery gallery = new ImageGallery(galleryImages);

            entityData.setData(ImageGallery.class, gallery);
        }
    }

    public <E extends Entity> void loadList(List<EntityData<E>> entities, LoadingOption... options)
    {
        for (EntityData<E> entityData : entities) {
            load(entityData);
        }
    }

    public Integer priority()
    {
        return 500;
    }
}
