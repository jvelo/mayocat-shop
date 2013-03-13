package org.mayocat.shop.catalog.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.mayocat.shop.catalog.model.Collection;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class CollectionMapper implements ResultSetMapper<Collection>
{
    @Override
    public Collection map(int index, ResultSet result, StatementContext statementContext) throws SQLException
    {
        Collection collection = new Collection(result.getLong("id"));
        collection.setSlug(result.getString("slug"));
        collection.setTitle(result.getString("title"));
        return collection;
    }
}
