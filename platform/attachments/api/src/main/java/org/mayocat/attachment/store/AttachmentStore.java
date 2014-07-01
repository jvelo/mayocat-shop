/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface AttachmentStore extends Store<Attachment, UUID>, EntityStore
{
    Attachment findBySlug(String slug);

    Attachment findBySlugAndExtension(String fileName, String extension);

    List<Attachment> findAllChildrenOf(Entity parent);

    List<Attachment> findAllChildrenOf(Entity parent, List<String> extensions);

    List<Attachment> findAllChildrenOfParentIds(List<UUID> parents);

    List<Attachment> findAllChildrenOfParentIds(List<UUID> parents, List<String> extensions);

    void detach(Attachment attachment) throws EntityDoesNotExistException;
}
