/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.object

import groovy.transform.CompileStatic

/**
 * API object for an entity image gallery
 *
 * @version $Id$
 */
@CompileStatic
class ImageGalleryApiObject
{
    List<ImageApiObject> images;
}
