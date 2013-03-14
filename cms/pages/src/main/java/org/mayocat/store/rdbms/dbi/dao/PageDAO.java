package org.mayocat.store.rdbms.dbi.dao;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.cms.pages.model.Page;
import org.mayocat.cms.pages.store.jdbi.mapper.PageMapper;
import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;
import org.skife.jdbi.v2.sqlobject.mixins.Transactional;
import org.skife.jdbi.v2.sqlobject.stringtemplate.UseStringTemplate3StatementLocator;

/**
 * @version $Id$
 */
@RegisterMapper(PageMapper.class)
@UseStringTemplate3StatementLocator
public abstract class PageDAO extends AbstractLocalizedEntityDAO<Page> implements Transactional<PageDAO>,
        PositionedDAO<Page>
{
    @SqlQuery
    public abstract Integer lastPosition(@BindBean("tenant") Tenant tenant);

    @SqlUpdate
    public abstract void createPage(@Bind("id") Long entityId, @Bind("position") Integer position,
            @BindBean("page") Page product);
}
