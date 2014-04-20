/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.CompileStatic
import org.hibernate.validator.constraints.NotBlank

/**
 * Represents a feature API object.
 *
 * @version $Id$
 */
@CompileStatic
class FeatureApiObject
{
    final String title;

    @NotBlank
    final String slug;
}
