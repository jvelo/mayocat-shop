package org.mayocat.image.internal;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

import javax.imageio.ImageIO;

import org.mayocat.image.ImageService;
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

    @Override
    public Optional<Rectangle> getFittingRectangle(Image image, Dimension dimension)
    {
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);

        double aspectRatio = (double) imageWidth / (double) imageHeight;
        double dimensionRatio = dimension.getWidth() / dimension.getHeight();

        if (aspectRatio == dimensionRatio) {
            // Exact ratio match
            return Optional.absent();
        }

        int width;
        int height;
        int x;
        int y;

        if (aspectRatio < dimensionRatio) {
            // Width is limitating, calculate height
            x = 0;
            width = imageWidth;
            height = Double.valueOf(width / dimensionRatio).intValue();
            y = (imageHeight - height) / 2;
        } else {
            // Height is limitating, calculate width
            y = 0;
            height = imageHeight;
            width = Double.valueOf(height * dimensionRatio).intValue();
            x = (imageWidth - width) / 2;
        }
        return Optional.of(new Rectangle(x, y, width, height));
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
