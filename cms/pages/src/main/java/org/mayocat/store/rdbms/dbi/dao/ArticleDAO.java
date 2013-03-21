package org.mayocat.store.rdbms.dbi.dao;

import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.jdbi.mapper.ArticleMapper;
import org.mayocat.store.rdbms.dbi.argument.DateAsTimestampArgumentFactory;
import org.mayocat.store.rdbms.jdbi.AddonsDAO;
import org.mayocat.store.rdbms.jdbi.AddonsHelper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(ArticleMapper.class)
@RegisterArgumentFactory(DateAsTimestampArgumentFactory.class)
@UseStringTemplate3StatementLocator
public abstract class ArticleDAO extends AbstractLocalizedEntityDAO<Article>
        implements Transactional<ArticleDAO>, AddonsDAO<Article>
{
    @SqlUpdate
    public abstract void createArticle(@Bind("id") Long entityId, @BindBean("article") Article article);

    @SqlUpdate
    public abstract Integer updateArticle(@BindBean("article") Article article);

    public void createOrUpdateAddons(Article entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }
}
