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
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.files.FileManager;
import org.mayocat.image.ImageService;
import org.mayocat.model.Attachment;
import org.mayocat.model.AttachmentData;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import com.google.common.base.Optional;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * @version $Id$
 */
@ComponentList({ DefaultImageService.class, DefaultImageProcessor.class })
public class DefaultImageServiceTest
{
    private FileManager fileManager;

    @Rule
    public final MockitoComponentMockingRule<ImageService> mocker =
            new MockitoComponentMockingRule(DefaultImageService.class, Arrays.asList(ImageProcessor.class));

    @Before
    public void setUp() throws Exception
    {
        this.fileManager = mocker.getInstance(FileManager.class);
        when(fileManager.resolvePermanentFilePath((Path) anyObject()))
                .thenReturn(Paths.get(System.getProperty("java.io.tmpdir")));
    }

    @Test
    public void testGetFittingBoxWhenWidthIsLimiting() throws Exception
    {
        // Height > Width
        InputStream data = this.getClass().getResourceAsStream("/120x200.gif");
        Attachment attachment = new Attachment();
        attachment.setData(new AttachmentData(data));

        Rectangle expected = new Rectangle(0, 50, 120, 100);
        Optional<Rectangle> box =
                this.mocker.getComponentUnderTest().getFittingRectangle(attachment, new Dimension(120, 100));

        Assert.assertEquals(expected, box.get());
    }

    @Test
    @Ignore
    // FIXME
    public void testGetFittingBoxWhenHeightIsLimiting() throws Exception
    {
        // Width > Height
        InputStream data = this.getClass().getResourceAsStream("/600x400.gif");
        Attachment attachment = new Attachment();
        attachment.setData(new AttachmentData(data));

        Rectangle expected = new Rectangle(100, 0, 400, 400);
        Optional<Rectangle> box =
                this.mocker.getComponentUnderTest().getFittingRectangle(attachment, new Dimension(150, 150));

        Assert.assertEquals(expected, box.get());

        expected = new Rectangle(50, 0, 300, 300);
        box = this.mocker.getComponentUnderTest().getFittingRectangle(attachment, new Dimension(150, 150));

        Assert.assertEquals(expected, box.get());
    }

    @Test
    public void testGetFittingBoxWhenAspectRatioMatchesDimensions() throws Exception
    {
        InputStream data = this.getClass().getResourceAsStream("/120x100.gif");
        Attachment attachment = new Attachment();
        attachment.setData(new AttachmentData(data));

        Optional<Rectangle> box =
                this.mocker.getComponentUnderTest().getFittingRectangle(attachment, new Dimension(12, 10));

        Assert.assertNull(box.orNull());
    }
}
