package org.mayocat.shop.grails.util

import java.awt.image.BufferedImage
import java.awt.RenderingHints

import javax.imageio.ImageIO

class ImageUtils
{

    static def crop(data, extension, size, params, out)
    {
        def bais = new ByteArrayInputStream(data)
        def originalImage = ImageIO.read(bais)
        def imageType = ((BufferedImage) originalImage).getType()
        def width = size.width
        def height = size.height
        def newImage = new BufferedImage(width, height, imageType)
        def graphics2D = newImage.createGraphics()
        def x2 = (params.x as Integer) + (params.width as Integer)
        def y2 = (params.y as Integer) + (params.height as Integer)
        if (!graphics2D.drawImage(originalImage, 0, 0, width, height, params.x as Integer, params.y as Integer, x2, y2, null))
        {
            // Conversion failed.
            throw new RuntimeException("Failed to resize image.")
        }
        ImageIO.write(newImage, extension, out)
        return out
    }
}
