/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image.store.memory;

import java.util.List;
import java.util.UUID;

import org.mayocat.image.model.Thumbnail;
import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.xwiki.component.annotation.Component;

/**
 * Not implemented yet.
 *
 * @version $Id$
 */
@Component("memory")
public class MemoryThumbnailStore implements ThumbnailStore
{
    public void createOrUpdateThumbnail(Thumbnail thumbnail)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<Thumbnail> findAll(Attachment attachment)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    public List<Thumbnail> findAllForIds(List<UUID> ids)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
