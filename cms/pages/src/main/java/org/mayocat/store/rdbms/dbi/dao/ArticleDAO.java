package org.mayocat.store.rdbms.dbi.dao;

import java.util.List;
import java.util.UUID;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.cms.news.model.Article;
import org.mayocat.cms.news.store.jdbi.mapper.ArticleMapper;
import org.mayocat.store.rdbms.dbi.argument.DateAsTimestampArgumentFactory;
import org.mayocat.store.rdbms.jdbi.AddonsDAO;
import org.mayocat.store.rdbms.jdbi.AddonsHelper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
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
    public abstract void createArticle(@BindBean("article") Article article);

    @SqlUpdate
    public abstract Integer updateArticle(@BindBean("article") Article article);

    @SqlQuery
    public abstract List<Article> findAllPublished(@BindBean("tenant") Tenant tenant, @Bind("number") Integer number,
            @Bind("offset") Integer offset);

    public void createOrUpdateAddons(Article entity)
    {
        AddonsHelper.createOrUpdateAddons(this, entity);
    }
}
