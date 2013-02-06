package org.mayocat.shop.image.internal;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.mayocat.shop.image.ImageService;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.mortennobel.imagescaling.ResampleOp;

/**
 * @version $Id$
 */
@Component
public class DefaultImageService implements ImageService
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
        return (RenderedImage) newImage;
    }

    @Override
    public Optional<Dimension> newDimension(Image image, Optional<Integer> width, Optional<Integer> height)
    {
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        if (imageHeight == height.or(imageHeight) && imageWidth == width.or(imageWidth)) {
            return Optional.absent();
        }

        int requestedWidth = width.or(-1);
        int requestedHeight = height.or(-1);
        int newWidth = imageWidth;
        int newHeight = imageHeight;

        double aspectRatio = (double) imageWidth / (double) imageHeight;

        if (requestedWidth <= 0 || requestedWidth >= imageWidth) {
            // Ignore the requested width. Check the requested height.
            if (requestedHeight > 0 && requestedHeight < imageHeight) {
                // Reduce the height, keeping aspect ratio.
                newWidth = (int) (requestedHeight * aspectRatio);
                newHeight = requestedHeight;
            }
        } else if (requestedHeight <= 0 || requestedHeight >= imageHeight) {
            // Ignore the requested height. Reduce the width, keeping aspect ratio.
            newWidth = requestedWidth;
            newHeight = (int) (requestedWidth / aspectRatio);
        } else {
            // Reduce the width and check if the corresponding height is less than the requested height.
            newWidth = requestedWidth;
            newHeight = (int) (requestedWidth / aspectRatio);
            if (newHeight > requestedHeight) {
                // We have to reduce the height instead and compute the width based on it.
                newWidth = (int) (requestedHeight * aspectRatio);
                newHeight = requestedHeight;
            }
        }

        if (newWidth != imageWidth && newHeight != imageHeight) {
            return Optional.of(new Dimension(newWidth, newHeight));
        } else {
            return Optional.absent();
        }
    }
}
