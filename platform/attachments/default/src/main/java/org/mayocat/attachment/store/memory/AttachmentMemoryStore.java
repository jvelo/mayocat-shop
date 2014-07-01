/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment.store.memory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.memory.BaseEntityMemoryStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
@Component("memory")
public class AttachmentMemoryStore extends BaseEntityMemoryStore<Attachment> implements AttachmentStore
{
    private Predicate<Attachment> withExtension(final String... extensions)
    {
        return new Predicate<Attachment>()
        {
            @Override
            public boolean apply(@Nullable Attachment input)
            {
                return input.getSlug().equals(input.getSlug()) &&
                        Arrays.asList(extensions).contains(input.getExtension());
            }
        };
    }

    @Override
    public Attachment findBySlug(final String slug)
    {
        return FluentIterable.from(entities.values()).filter(withSlug(slug)).limit(1).first().orNull();
    }

    @Override
    public Attachment findBySlugAndExtension(final String fileName, final String extension)
    {
        return FluentIterable.from(entities.values()).filter(withExtension(extension)).limit(1).first().orNull();
    }

    @Override
    public List<Attachment> findAllChildrenOf(Entity parent)
    {
        return FluentIterable.from(entities.values()).filter(withParent(parent)).toList();
    }

    @Override
    public List<Attachment> findAllChildrenOf(Entity parent, List<String> extensions)
    {
        return FluentIterable.from(entities.values()).filter(withParent(parent)).filter(withExtension(
                extensions.toArray(new String[extensions.size()]))).toList();
    }

    @Override
    public List<Attachment> findAllChildrenOfParentIds(List<UUID> parents)
    {
        return FluentIterable.from(entities.values()).filter(
                withParentId(parents.toArray(new UUID[parents.size()]))).toList();
    }

    @Override
    public List<Attachment> findAllChildrenOfParentIds(List<UUID> parents, List<String> extensions)
    {
        return FluentIterable.from(entities.values()).filter(
                withParentId(parents.toArray(new UUID[parents.size()]))).filter(withExtension(
                extensions.toArray(new String[extensions.size()]))).toList();
    }

    @Override
    public void detach(Attachment attachment) throws EntityDoesNotExistException
    {
        Attachment found = findById(attachment.getId());
        found.setParentId(null);
    }
}
