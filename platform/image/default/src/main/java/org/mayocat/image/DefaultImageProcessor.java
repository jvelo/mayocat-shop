/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.xwiki.component.annotation.Component;

import com.mortennobel.imagescaling.ResampleOp;

/**
 * AWT-based image processor
 *
 * @version $Id$
 */
@Component
public class DefaultImageProcessor implements ImageProcessor
{
    @Override
    public Image readImage(InputStream inputStream) throws IOException
    {
        return ImageIO.read(inputStream);
    }

    @Override
    public RenderedImage scaleImage(Image image, Dimension dimension)
    {
        ResampleOp resampleOp = new ResampleOp(
                (int) Math.round(dimension.getWidth()),
                (int) Math.round(dimension.getHeight())
        );
        BufferedImage newImage = resampleOp.filter((BufferedImage) image, null);
        return newImage;
    }

    @Override
    public RenderedImage cropImage(Image image, Rectangle boundaries)
    {
        BufferedImage original = (BufferedImage) image;

        if (boundaries != null) {
            try {
                original = original.getSubimage(new Double(boundaries.getX()).intValue(),
                        new Double(boundaries.getY()).intValue(), new Double(boundaries.getWidth()).intValue(),
                        new Double(boundaries.getHeight()).intValue());
            } catch (RasterFormatException e) {
                // Nevermind, we will use the original image, not cropped
            }
        }
        return original;
    }
}
