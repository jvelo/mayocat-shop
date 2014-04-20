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

import org.xwiki.component.annotation.Role;

/**
 * Image processor interface
 *
 * @version $Id$
 */
@Role
public interface ImageProcessor
{
    /**
     * Reads an image from an stream
     *
     * @param inputStream the input stream to read the image from
     * @return the read image
     * @throws java.io.IOException when the image could not be read
     */
    Image readImage(InputStream inputStream) throws IOException;

    /**
     * Resizes an image to the passed dimension
     *
     * @param image the image to scale
     * @param dimension the dimension (width and height to scale to)
     * @return the scaled image as a rendered image
     */
    RenderedImage scaleImage(Image image, Dimension dimension);

    /**
     * Crops an image in the passed rectangle boundaries
     *
     * @param image the image to crop
     * @param boundaries the rectangle to crop the image in
     * @return the cropped image as a rendered image
     */
    RenderedImage cropImage(Image image, Rectangle boundaries);
}
