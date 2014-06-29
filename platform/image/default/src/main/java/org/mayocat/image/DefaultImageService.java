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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.imageio.ImageIO;
import javax.inject.Inject;

import org.mayocat.files.FileManager;
import org.mayocat.model.Attachment;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Function;
import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Component
public class DefaultImageService implements ImageService, Initializable
{
    @Inject
    private FileManager fileManager;

    @Inject
    private ImageProcessor imageProcessor;

    @Inject
    private Logger logger;

    private Path imageFileCache;

    private Function<InputStream, Image> loadImage;

    public void initialize() throws InitializationException
    {
        loadImage = new LoadImageFunction(imageProcessor);
        imageFileCache = fileManager.resolvePermanentFilePath(Paths.get("imagecache"));
        if (!imageFileCache.toFile().isDirectory()) {
            // The image cache directory for this image does not exist, create it
            try {
                Files.createDirectory(imageFileCache);
            } catch (IOException e) {
                throw new InitializationException("Failed to initialize image cache. Is the data directory writable ?");
            }
        }
    }

    public InputStream getImage(Attachment attachment, Dimension dimension) throws IOException
    {
        Path imageDirectory = getImageCacheDirectoryPath(attachment);

        String dimensionFileName = getDimensionFileName(attachment, dimension);

        File dimensionFile = imageDirectory.resolve(dimensionFileName).toFile();
        if (!dimensionFile.isFile()) {
            Image image = attachment.getData().getObject(loadImage, Image.class);
            RenderedImage scaled;
            scaled = imageProcessor.scaleImage(image, dimension);
            ImageIO.write(scaled, attachment.getExtension(), dimensionFile);
        }
        return new java.io.FileInputStream(dimensionFile);
    }

    public InputStream getImage(Attachment attachment, Dimension dimension, Rectangle rectangle)
            throws IOException
    {
        Path imageDirectory = getImageCacheDirectoryPath(attachment);

        String boxDirectory = getBoxDirectoryName(rectangle);

        String dimensionFileName = getDimensionFileName(attachment, dimension);

        File dimensionFile = imageDirectory.resolve(boxDirectory).resolve(dimensionFileName).toFile();
        if (!dimensionFile.isFile()) {
            Image image = attachment.getData().getObject(loadImage, Image.class);
            RenderedImage cropped = imageProcessor.cropImage(image, rectangle);
            RenderedImage scaled = imageProcessor.scaleImage((Image) cropped, dimension);
            dimensionFile.mkdirs();
            ImageIO.write(scaled, attachment.getExtension(), dimensionFile);
        }

        return new java.io.FileInputStream(dimensionFile);
    }

    private String getDimensionFileName(Attachment attachment, Dimension dimension)
    {
        return String.valueOf(Math.round(dimension.getWidth())) + "x" +
                String.valueOf(Math.round(dimension.getHeight())) + "." + attachment.getExtension();
    }

    public InputStream getImage(Attachment attachment, Rectangle rectangle)
            throws IOException
    {
        Path imageDirectory = getImageCacheDirectoryPath(attachment);

        String boxDirectory = getBoxDirectoryName(rectangle);

        String dimensionFileName = attachment.getSlug() + "." + attachment.getExtension();

        File dimensionFile = imageDirectory.resolve(boxDirectory).resolve(dimensionFileName).toFile();
        if (!dimensionFile.isFile()) {
            Image image = attachment.getData().getObject(loadImage, Image.class);
            RenderedImage cropped = imageProcessor.cropImage(image, rectangle);
            dimensionFile.mkdirs();
            ImageIO.write(cropped, attachment.getExtension(), dimensionFile);
        }

        return new java.io.FileInputStream(dimensionFile);
    }

    private String getBoxDirectoryName(Rectangle rectangle)
    {
        return String.valueOf(Math.round(rectangle.getX())) + "-" +
                String.valueOf(Math.round(rectangle.getY())) + "-" +
                String.valueOf(Math.round(rectangle.getWidth())) + "-" +
                String.valueOf(Math.round(rectangle.getHeight()));
    }

    public Optional<Rectangle> getFittingRectangle(Attachment attachment, Dimension dimension) throws IOException
    {
        int imageWidth = -1;
        int imageHeight = -1;

        if (attachment.getMetadata().containsKey("imageDimensions")) {
            // First, try to exploit stored metadata

            imageWidth = (int) attachment.getMetadata().get("imageDimensions").get("width");
            imageHeight = (int) attachment.getMetadata().get("imageDimensions").get("height");
        }
        else {
            // Fallback on loading the image
            Image image = attachment.getData().getObject(loadImage, Image.class);

            imageWidth = image.getWidth(null);
            imageHeight = image.getHeight(null);
        }

        if (imageWidth < 0 || imageHeight < 0) {
            return Optional.absent();
        }

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

    public Optional<Dimension> newDimension(Attachment attachment, Optional<Integer> width, Optional<Integer> height)
            throws IOException
    {
        Image image = attachment.getData().getObject(loadImage, Image.class);

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

    @Override
    public Optional<Dimension> newDimension(Rectangle boundaries, Optional<Integer> width, Optional<Integer> height)
            throws IOException
    {
        int imageWidth = (int) Math.round(boundaries.getWidth());
        int imageHeight = (int) Math.round(boundaries.getHeight());

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

    private Path getImageCacheDirectoryPath(Attachment attachment)
    {
        String baseDirectory = attachment.getId().toString().substring(0, 2);
        Path imageDirectory = imageFileCache.resolve(baseDirectory).resolve(attachment.getId().toString());
        if (!imageDirectory.toFile().isDirectory()) {
            // The image cache directory for this image does not exist, create it
            imageDirectory.toFile().mkdirs();
        }
        return imageDirectory;
    }
}
