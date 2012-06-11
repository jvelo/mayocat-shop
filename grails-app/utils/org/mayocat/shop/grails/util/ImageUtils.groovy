package org.mayocat.shop.grails.util

import com.mortennobel.imagescaling.ResampleOp
import java.awt.image.BufferedImage

import javax.imageio.ImageIO

class ImageUtils
{

    static def cropAndResize(data, extension, size, params, out)
    {   
        def bais = new ByteArrayInputStream(data)
        def originalImage = ImageIO.read(bais)
        def imageType = ((BufferedImage) originalImage).getType()
        def width = size.width
        def height = size.height
        
        // Extract the part of the image to get reiszed
        def selection = ((BufferedImage) originalImage).getSubimage(
            params.x as Integer,
            params.y as Integer,
            params.width as Integer,
            params.height as Integer
        )

        // Rezise        
        ResampleOp  resampleOp = new ResampleOp (width, height);
        BufferedImage newImage = resampleOp.filter(selection, null);
        
        ImageIO.write(newImage, extension, out)
        return out
    }
}
