package org.mayocat.shop.store.rdbms.dbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.model.Category;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class CategoryMapper implements ResultSetMapper<Category>
{
    @Override
    public Category map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        Category category = new Category(result.getLong("id"));
        category.setSlug(result.getString("slug"));
        category.setTitle(result.getString("title"));
        return category;
    }
}
