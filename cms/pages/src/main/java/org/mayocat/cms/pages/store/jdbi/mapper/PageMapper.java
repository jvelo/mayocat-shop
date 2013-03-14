package org.mayocat.cms.pages.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.cms.pages.model.Page;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

/**
 * @version $Id$
 */
public class PageMapper implements ResultSetMapper<Page>
{
    @Override
    public Page map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Page page = new Page(resultSet.getLong("id"));
        page.setTitle(resultSet.getString("title"));
        page.setSlug(resultSet.getString("slug"));
        page.setContent(resultSet.getString("content"));

        return page;
    }
}
