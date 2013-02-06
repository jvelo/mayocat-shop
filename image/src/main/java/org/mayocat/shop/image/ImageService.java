package org.mayocat.shop.image;

import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Role
public interface ImageService
{
    /**
     * Reads an image from an stream
     *
     * @param inputStream the input stream to read the image from
     * @return the read image
     * @throws IOException when the image could not be read
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
    Optional<Dimension> newDimension(Image image, Optional<Integer> width, Optional<Integer> height);
}
