package org.mayocat.cms.news.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.cms.news.model.Article;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ArticleStore extends Store<Article, UUID>, EntityStore
{
    Article findBySlug(String slug);

    List<Article> findAllPublished(Integer offset, Integer number);

    Integer countAllPublished();
}
