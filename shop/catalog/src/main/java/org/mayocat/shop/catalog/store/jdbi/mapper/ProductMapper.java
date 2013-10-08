package org.mayocat.shop.catalog.store.jdbi.mapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.LocaleUtils;
import org.mayocat.shop.catalog.model.Product;
import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;

public class ProductMapper implements ResultSetMapper<Product>
{
    @Override
    public Product map(int index, ResultSet resultSet, StatementContext statementContext) throws SQLException
    {
        Product product = new Product((UUID) resultSet.getObject("id"));
        product.setSlug(resultSet.getString("slug"));
        product.setTitle(resultSet.getString("title"));
        product.setDescription(resultSet.getString("description"));
        if (resultSet.getObject("on_shelf") != null) {
            product.setOnShelf(resultSet.getBoolean("on_shelf"));
        }
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setWeight(resultSet.getBigDecimal("weight"));
        UUID featuredImageId = (UUID) resultSet.getObject("featured_image_id");
        if (featuredImageId != null) {
            product.setFeaturedImageId(featuredImageId);
        }

        if (!Strings.isNullOrEmpty(resultSet.getString("localization_data"))) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<Locale, Map<String, Object>> localizedVersions = Maps.newHashMap();
                Map[] data = mapper.readValue(resultSet.getString("localization_data"), Map[].class);
                for (Map map : data) {
                    localizedVersions.put(LocaleUtils.toLocale((String) map.get("locale")), (Map) map.get("entity"));
                }
                product.setLocalizedVersions(localizedVersions);
            } catch (IOException e) {
                throw new SQLException("Failed to de-serialize localization JSON data", e);
            }
        }

        String model = resultSet.getString("model");
        if (!Strings.isNullOrEmpty(model)) {
            product.setModel(model);
        }
        return product;
    }
}
