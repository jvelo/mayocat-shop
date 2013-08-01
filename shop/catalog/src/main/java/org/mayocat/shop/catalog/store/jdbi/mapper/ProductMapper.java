package org.mayocat.shop.catalog.store.jdbi.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Product;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.google.common.base.Strings;

public class ProductMapper implements ResultSetMapper<Product>
{
    @Override
    public Product map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException
    {
        Product product = new Product((UUID) resultSet.getObject("id"));
        product.setSlug(resultSet.getString("slug"));
        product.setTitle(resultSet.getString("title"));
        product.setDescription(resultSet.getString("description"));
        product.setOnShelf(resultSet.getBoolean("on_shelf"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setWeight(resultSet.getBigDecimal("weight"));
        UUID featuredImageId = (UUID) resultSet.getObject("featured_image_id");
        if (featuredImageId != null) {
            product.setFeaturedImageId(featuredImageId);
        }

        String model = resultSet.getString("model");
        if (!Strings.isNullOrEmpty(model)) {
            product.setModel(model);
        }
        return product;
    }
}
