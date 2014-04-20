/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic

/**
 * Web object holding an entity images. Contains a mapping for "all" images and for the "featured" image.
 *
 * @version $Id$
 */
@CompileStatic
class EntityImagesWebObject
{
    ImageWebObject featured

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ImageWebObject> all
}
