/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.pages.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.cms.pages.model.Page;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface PageStore extends Store<Page, UUID>, EntityStore
{
    Page findBySlug(String slug);

    List<Page> findAllRootPages();
}
