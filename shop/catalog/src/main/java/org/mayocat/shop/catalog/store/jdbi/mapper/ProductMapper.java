/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.store.jdbi.mapper;

import java.io.IOException;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.rdbms.dbi.mapper.MapperUtils;
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
        if (resultSet.getObject("parent_id") != null) {
            product.setParentId((UUID) resultSet.getObject("parent_id"));
        }
        product.setSlug(resultSet.getString("slug"));
        product.setTitle(resultSet.getString("title"));
        product.setDescription(resultSet.getString("description"));
        if (resultSet.getObject("on_shelf") != null) {
            product.setOnShelf(resultSet.getBoolean("on_shelf"));
        }
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setWeight(resultSet.getBigDecimal("weight"));
        if (resultSet.getObject("stock") != null) {
            product.setStock(resultSet.getInt("stock"));
        }
        product.setVirtual(resultSet.getBoolean("virtual"));
        UUID featuredImageId = (UUID) resultSet.getObject("featured_image_id");
        if (featuredImageId != null) {
            product.setFeaturedImageId(featuredImageId);
        }

        if (MapperUtils.hasColumn("localization_data", resultSet) &&
                !Strings.isNullOrEmpty(resultSet.getString("localization_data")))
        {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Map<Locale, Map<String, Object>> localizedVersions = Maps.newHashMap();
                Map[] data = mapper.readValue(resultSet.getString("localization_data"), Map[].class);
                for (Map map : data) {

                    localizedVersions.put(Locale.forLanguageTag((String) map.get("locale")), (Map) map.get("entity"));
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
        String type = resultSet.getString("product_type");
        if (!Strings.isNullOrEmpty(type)) {
            product.setType(type);
        }

        if (resultSet.getArray("features") != null) {
            // There's no support for getting the pg uuid array as a Java UUID array (or even String array) at the time
            // this is written, we have to iterate over the array own result set and construct the Java array ourselves
            List<UUID> ids = new ArrayList<>();
            Array array = resultSet.getArray("features");
            if (array != null) {
                ResultSet featuresResultSet = array.getResultSet();
                while (featuresResultSet.next()) {
                    ids.add((UUID) featuresResultSet.getObject("value"));
                }
                product.setFeatures(ids);
            }
        }
        return product;
    }
}
