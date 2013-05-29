package org.mayocat.image;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.image.ImageService;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@ComponentList({ DefaultImageService.class })
public class DefaultImageServiceTest
{
    @Rule
    public final MockitoComponentMockingRule<ImageService> mocker =
            new MockitoComponentMockingRule(DefaultImageService.class);

    @Test
    public void testGetFittingBoxWhenWidthIsLimiting() throws Exception
    {
        // Height > Width

        BufferedImage image = new BufferedImage(120, 200, BufferedImage.TYPE_INT_ARGB);

        Rectangle expected = new Rectangle(0, 50, 120, 100);
        Optional<Rectangle> box =
                this.mocker.getComponentUnderTest().getFittingRectangle(image, new Dimension(120, 100));

        Assert.assertEquals(expected, box.get());
    }

    @Test
    public void testGetFittingBoxWhenHeightIsLimiting() throws Exception
    {
        // Width > Height

        BufferedImage image = new BufferedImage(600, 400, BufferedImage.TYPE_INT_ARGB);

        Rectangle expected = new Rectangle(100, 0, 400, 400);
        Optional<Rectangle> box =
                this.mocker.getComponentUnderTest().getFittingRectangle(image, new Dimension(150, 150));

        Assert.assertEquals(expected, box.get());

        image = new BufferedImage(400, 300, BufferedImage.TYPE_INT_ARGB);

        expected = new Rectangle(50, 0, 300, 300);
        box = this.mocker.getComponentUnderTest().getFittingRectangle(image, new Dimension(150, 150));

        Assert.assertEquals(expected, box.get());
    }

    @Test
    public void testGetFittingBoxWhenAspectRatioMatchesDimensions() throws Exception
    {
        BufferedImage image = new BufferedImage(120, 100, BufferedImage.TYPE_INT_ARGB);
        Optional<Rectangle> box = this.mocker.getComponentUnderTest().getFittingRectangle(image, new Dimension(12, 10));

        Assert.assertNull(box.orNull());
    }
}
