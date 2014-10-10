/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.awt.*;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.attachment.model.LoadedAttachment;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.files.FileManager;
import org.mayocat.attachment.model.Attachment;
import org.mayocat.attachment.model.AttachmentData;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import com.google.common.base.Optional;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * @version $Id$
 *
 * FIXME: fails with "can't find descriptor for component AttachmentStore"
 */
@Ignore
@ComponentList({ DefaultImageService.class, DefaultImageProcessor.class })
public class DefaultImageServiceTest
{
    private FileManager fileManager;

    private AttachmentStore attachmentStore;

    @Rule
    public final MockitoComponentMockingRule<ImageService> mocker =
            new MockitoComponentMockingRule(DefaultImageService.class, Arrays.asList(ImageProcessor.class));

    @Before
    public void setUp() throws Exception
    {
        this.fileManager = mocker.getInstance(FileManager.class);
        when(fileManager.resolvePermanentFilePath((Path) anyObject()))
                .thenReturn(Paths.get(System.getProperty("java.io.tmpdir")));

        this.attachmentStore = mocker.getInstance(AttachmentStore.class);
    }

    @Test
    public void testGetFittingBoxWhenWidthIsLimiting() throws Exception
    {
        // Height > Width
        InputStream data = this.getClass().getResourceAsStream("/120x200.gif");
        LoadedAttachment attachment = new LoadedAttachment();
        attachment.setData(new AttachmentData(data));

        when(attachmentStore.findAndLoadById((UUID) anyObject()))
                .thenReturn(attachment);

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
        LoadedAttachment attachment = new LoadedAttachment();
        attachment.setData(new AttachmentData(data));

        when(attachmentStore.findAndLoadById((UUID) anyObject()))
                .thenReturn(attachment);

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
        LoadedAttachment attachment = new LoadedAttachment();
        attachment.setData(new AttachmentData(data));

        when(attachmentStore.findAndLoadById((UUID) anyObject()))
                .thenReturn(attachment);

        Optional<Rectangle> box =
                this.mocker.getComponentUnderTest().getFittingRectangle(attachment, new Dimension(12, 10));

        Assert.assertNull(box.orNull());
    }
}
