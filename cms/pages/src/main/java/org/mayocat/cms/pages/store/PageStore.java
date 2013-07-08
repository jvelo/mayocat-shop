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
