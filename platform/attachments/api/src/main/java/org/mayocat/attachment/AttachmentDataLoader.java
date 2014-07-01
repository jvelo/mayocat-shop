/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.entity.DataLoaderAssistant;
import org.mayocat.entity.EntityData;
import org.mayocat.entity.LoadingOption;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.model.HasFeaturedImage;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
@Component("attachments")
public class AttachmentDataLoader implements DataLoaderAssistant
{
    @Inject
    private AttachmentStore attachmentStore;

    @Inject
    private Logger logger;

    public <E extends Entity> void load(EntityData<E> entity, LoadingOption... options)
    {
        entity.setChildren(Attachment.class, this.attachmentStore.findAllChildrenOf(entity.getEntity()));
    }

    public <E extends Entity> void loadList(List<EntityData<E>> entities, LoadingOption... options)
    {
        List<LoadingOption> optionsAsList = Arrays.asList(options);

        List<Attachment> attachments;

        if (optionsAsList.contains(AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)) {

            // Featured image only : we ignore all other attachments

            List<UUID> imageIds = FluentIterable.from(entities).transform(new Function<EntityData<E>, UUID>()
            {
                public UUID apply(EntityData<E> input)
                {
                    if (!HasFeaturedImage.class.isAssignableFrom(input.getEntity().getClass())) {
                        throw new RuntimeException(
                                "Failed to load attachment list with featured image only option : entity does not implement HasFeaturedImage");
                    }
                    return ((HasFeaturedImage) input.getEntity()).getFeaturedImageId();
                }
            }).filter(Predicates.notNull()).toList();

            attachments = imageIds.size() > 0 ? this.attachmentStore.findByIds(imageIds) :
                    Collections.<Attachment>emptyList();
        } else {

            // All attachments for the list

            List<UUID> ids = FluentIterable.from(entities).transform(new Function<EntityData<E>, UUID>()
            {
                public UUID apply(EntityData<E> input)
                {
                    return input.getEntity().getId();
                }
            }).toList();

            attachments = ids.size() > 0 ? this.attachmentStore.findAllChildrenOfParentIds(ids) :
                    Collections.<Attachment>emptyList();
        }

        for (final EntityData entity : entities) {
            List<Attachment> thisEntityAttachments =
                    FluentIterable.from(attachments).filter(new Predicate<Attachment>()
                    {
                        public boolean apply(Attachment attachment)
                        {
                            return entity.getEntity().getId().equals(attachment.getParentId());
                        }
                    }).toList();
            entity.setChildren(Attachment.class, thisEntityAttachments);
        }
    }

    public Integer priority()
    {
        return 1000;
    }
}
