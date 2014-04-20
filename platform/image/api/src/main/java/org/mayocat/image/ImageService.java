/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

import org.mayocat.model.Attachment;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Role
public interface ImageService
{
    InputStream getImage(Attachment attachment, Dimension dimension) throws IOException;

    InputStream getImage(Attachment attachment, Dimension dimension, Rectangle rectangle) throws IOException;

    InputStream getImage(Attachment attachment, Rectangle rectangle) throws IOException;

    /**
     * Computes the largest rectangle of the passed image that respect the passed dimension ratio. Cropped areas are
     * divided equally at the two extremities (top/bottom or left/right).
     *
     * @param attachment the image to get the bounding rectangle for
     * @param dimension the dimension (width vs. height ratio) to fit
     * @return the image boundaries as an optional rectangle. If the rectangle is absent, it means there is an exact
     * match between the dimension passed and the image aspect ratio, so that no cropping is necessary.
     */
    Optional<Rectangle> getFittingRectangle(Attachment attachment, Dimension dimension) throws IOException;

    /**
     * Computes the dimension (width and height) an image will have, respecting its original aspect ratio when adapting
     * one of its original dimension (width or height). If a value for both dimension it set by the caller, this method
     * will compute the largest image that can fit both new dimension, meaning one of the actual computed width or
     * height could be smaller than request ; but the image aspect ratio will always be respected. If the new dimensions
     * matches exactly the original image dimensions, this returns an absent option.
     *
     * @param image the image to compute new dimensions for
     * @param width an option of a width of the new image dimension
     * @param height an option of a height of the new image dimension
     * @return either a new dimension or an absent option if the dimensions matches exactly the original image
     */
    Optional<Dimension> newDimension(Attachment image, Optional<Integer> width, Optional<Integer> height)
            throws IOException;

    Optional<Dimension> newDimension(Rectangle boundaries, Optional<Integer> width, Optional<Integer> height)
            throws IOException;
}
