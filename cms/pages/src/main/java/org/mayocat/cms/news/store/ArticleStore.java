package org.mayocat.cms.news.store;

import org.mayocat.cms.news.model.Article;
import org.mayocat.store.EntityStore;
import org.mayocat.store.Store;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ArticleStore extends Store<Article, Long>, EntityStore
{
    Article findBySlug(String slug);
}
