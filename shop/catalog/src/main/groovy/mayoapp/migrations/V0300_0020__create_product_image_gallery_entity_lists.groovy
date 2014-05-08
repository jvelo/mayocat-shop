/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package mayoapp.migrations

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import groovy.transform.CompileStatic
import org.mayocat.flyway.migrations.AbstractImageGalleryEntityListsMigration

/**
 * Create entity_list for all products images.
 *
 * @version $Id$
 */
@CompileStatic
class V0300_0020__create_product_image_gallery_entity_lists extends AbstractImageGalleryEntityListsMigration implements JdbcMigration
{
    String getTable()
    {
        "product"
    }
}
