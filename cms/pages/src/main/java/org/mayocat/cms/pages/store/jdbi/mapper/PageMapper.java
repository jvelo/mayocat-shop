package org.mayocat.cms.pages.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.cms.pages.model.Page;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Strings;

/**
 * @version $Id$
 */
public class PageMapper implements ResultSetMapper<Page>
{
    @Override
    public Page map(int index, ResultSet resultSet, StatementContext ctx) throws SQLException
    {
        Page page = new Page((UUID) resultSet.getObject("id"));
        page.setPublished(resultSet.getBoolean("published"));
        page.setTitle(resultSet.getString("title"));
        page.setSlug(resultSet.getString("slug"));
        page.setContent(resultSet.getString("content"));

        page.setFeaturedImageId((UUID) resultSet.getObject("featured_image_id"));

        String model = resultSet.getString("model");
        if (!Strings.isNullOrEmpty(model)) {
            page.setModel(model);
        }

        return page;
    }
}
