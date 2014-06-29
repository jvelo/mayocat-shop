/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.log4j.spi.LoggerFactory;
import org.mayocat.attachment.MetadataExtractor;
import org.mayocat.attachment.util.AttachmentUtils;
import org.mayocat.model.Attachment;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Component("imageDimensions")
public class ImageDimensionsMetadataExtractor implements MetadataExtractor
{
    @Inject
    private ImageProcessor imageProcessor;

    @Inject
    private Logger logger;

    /**
     * Constructor for DI
     */
    public ImageDimensionsMetadataExtractor()
    {
    }

    /**
     * Constructor to be used outside the DI realm (tests, migrations, etc.)
     *
     * @param imageProcessor the imageProcessor to use
     */
    public ImageDimensionsMetadataExtractor(ImageProcessor imageProcessor)
    {
        this.imageProcessor = imageProcessor;
        this.logger = org.slf4j.LoggerFactory.getLogger(ImageDimensionsMetadataExtractor.class);
    }

    public Optional<Map<String, Object>> extractMetadata(Attachment attachment)
    {
        if (!AttachmentUtils.isImage(attachment)) {
            return Optional.absent();
        }

        try {
            Image image = attachment.getData().getObject(new LoadImageFunction(imageProcessor), Image.class);

            final int imageWidth = image.getWidth(null);
            final int imageHeight = image.getHeight(null);

            image.flush();

            Map<String, Object> result = new HashMap<String, Object>()
            {
                {
                    put("width", imageWidth);
                    put("height", imageHeight);
                }
            };

            return Optional.of(result);
        } catch (Exception e) {
            logger.warn("Failed to extract image dimensions for attachment {} : {} ", attachment.getFilename(),  e.getMessage());
            return Optional.absent();
        }
    }
}
