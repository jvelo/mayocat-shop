package org.mayocat.cms.pages.store.memory;

import java.util.List;

import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.PageStore;
import org.mayocat.store.memory.BaseEntityMemoryStore;

import com.google.common.collect.FluentIterable;

/**
 * In-memory implementation of {@link PageStore}.
 *
 * @version $Id$
 */
public class MemoryPageStore extends BaseEntityMemoryStore<Page> implements PageStore
{
    @Override
    public Page findBySlug(String slug)
    {
        return FluentIterable.from(all()).filter(withSlug(slug)).first().orNull();
    }

    @Override
    public List<Page> findAllRootPages()
    {
        return FluentIterable.from(all()).filter(withParent(null)).toList();
    }
}
