/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.web.object

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.util.ImageUtils
/**
 * @version $Id$
 */
@CompileStatic
class AbstractImageWebObject
{
    @Delegate
    HashMap map = new HashMap();

    protected def Optional<Thumbnail> findBestFit(Image image, Integer width, Integer height)
    {
        if (!width || !height) {
            // First handle the case where we have only one dimension width or height
            for (Thumbnail thumbnail : image.thumbnails) {
                if ((thumbnail.ratio.equals("1:0") && !height) ||
                        (thumbnail.ratio.equals("0:1") && !width))
                {
                    return Optional.of(thumbnail);
                }
            }
            return Optional.absent();
        }

        // Then handle the general case where we have both dimensions width and height
        Thumbnail foundRatio = null;
        String expectedRatio = ImageUtils.imageRatio(width, height);

        for (Thumbnail thumbnail : image.thumbnails) {
            if (thumbnail.ratio.equals(expectedRatio)) {
                if (thumbnail.width.equals(width)
                        && thumbnail.height.equals(height))
                {
                    // Exact match, stop searching
                    return Optional.of(thumbnail);
                } else {
                    // Ratio match, keep searching
                    foundRatio = thumbnail;
                }
            }
        }
        return Optional.fromNullable(foundRatio);
    }
}
