/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.api.object

import groovy.transform.CompileStatic

import javax.validation.constraints.NotNull

/**
 * The API object for a {@link org.mayocat.image.model.Thumbnail} of an image.
 *
 * @version $Id$
 */
@CompileStatic
class ImageThumbnailApiObject
{
    @NotNull
    String source;

    @NotNull
    String hint;

    String ratio;

    @NotNull
    Integer x;

    @NotNull
    Integer y;

    @NotNull
    Integer width;

    @NotNull
    Integer height;
}
