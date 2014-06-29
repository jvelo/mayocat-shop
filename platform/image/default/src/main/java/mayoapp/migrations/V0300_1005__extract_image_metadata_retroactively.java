/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.migrations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.mayocat.flyway.migrations.PG_UUID;
import org.mayocat.image.DefaultImageProcessor;
import org.mayocat.image.ImageDimensionsMetadataExtractor;
import org.mayocat.image.ImageProcessor;
import org.mayocat.jdbi.StatementContextStub;
import org.mayocat.model.Attachment;
import org.mayocat.store.rdbms.dbi.mapper.AttachmentMapper;
import org.skife.jdbi.v2.StatementContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;

/**
 * @version $Id$
 */
public class V0300_1005__extract_image_metadata_retroactively implements JdbcMigration
{
    private Logger logger = LoggerFactory.getLogger(V0300_1005__extract_image_metadata_retroactively.class);

    @Override
    public void migrate(Connection connection) throws Exception
    {
        ImageProcessor imageProcessor = new DefaultImageProcessor();
        ImageDimensionsMetadataExtractor extractor = new ImageDimensionsMetadataExtractor(imageProcessor);

        StatementContext context = new StatementContextStub();
        connection.setAutoCommit(false);
        Statement countStatement = connection.createStatement();

        Integer count = 0;
        ResultSet res = countStatement
                .executeQuery(
                        "SELECT COUNT(*) FROM attachment JOIN entity on attachment.entity_id = entity.id"); //WHERE parent_id is not null
        while (res.next()) {
            count = res.getInt(1);
        }
        countStatement.close();

        Integer i = 0;

        Map<UUID, Object> toSave = new HashMap<>();

        for (int offset = 0; offset < count; offset += 50) {
            Statement queryStatement = connection.createStatement();
            ResultSet data = queryStatement.executeQuery(
                    "SELECT * from attachment JOIN entity on attachment.entity_id = entity.id LIMIT 50 OFFSET " +
                            offset);

            while (data.next()) {
                AttachmentMapper mapper = new AttachmentMapper();
                Attachment attachment = mapper.map(0, data, context);

                logger.info("Processing attachment " + i + " : " + attachment.getFilename());

                Optional<Map<String, Object>> metadata = extractor.extractMetadata(attachment);

                if (metadata.isPresent()) {
                    Map<String, Map<String, Object>> meta = new HashMap<>(attachment.getMetadata());
                    meta.put("imageDimensions", metadata.get());
                    toSave.put(attachment.getId(), meta);
                }

                i++;
            }

            queryStatement.close();
        }

        ObjectMapper mapper = new ObjectMapper();
        PreparedStatement statement = connection.prepareStatement(
                "UPDATE attachment SET metadata = CAST (? AS json) WHERE entity_id =?");

        for (UUID attachment : toSave.keySet()) {
            statement.setObject(2, new PG_UUID(attachment));
            statement.setObject(1, mapper.writeValueAsString(toSave.get(attachment)));
            statement.addBatch();
            logger.info("Adding image to batch " + i + " : " + attachment.toString());
        }

        statement.executeBatch();
    }
}
