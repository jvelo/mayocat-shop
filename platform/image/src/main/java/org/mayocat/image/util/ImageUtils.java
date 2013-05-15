package org.mayocat.image.util;

import java.util.HashSet;
import java.util.Set;

import com.google.common.math.IntMath;

/**
 * @version $Id$
 */
public class ImageUtils
{
    /**
     * @param width the width of the image to compute the ratio for
     * @param height the height of the image to compute the ratio for
     * @return the image ratio, under the form <code</>x:y</code> where x represents the horizontal dimension and y the
     *         vertical dimension of the ratio
     */
    public static String imageRatio(Integer width, Integer height)
    {
        Integer gcd = IntMath.gcd(width, height);
        return (width / gcd) + ":" + (height / gcd);
    }

}
