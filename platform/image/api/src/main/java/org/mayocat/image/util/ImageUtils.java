/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image.util;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Preconditions;
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
        Preconditions.checkArgument(!(width == 0 && height == 0),
                "Cannot compute image ration when both width and height are zero");

        Integer gcd = IntMath.gcd(width, height);
        return (width / gcd) + ":" + (height / gcd);
    }

}
