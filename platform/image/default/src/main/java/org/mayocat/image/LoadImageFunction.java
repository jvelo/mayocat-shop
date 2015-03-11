/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Function;

/**
 * Function that loads an java.awt.Image from an input stream.
 *
 * @version $Id$
 */
public class LoadImageFunction implements Function<InputStream, Image>
{
    private ImageProcessor imageProcessor;

    public LoadImageFunction(ImageProcessor imageProcessor)
    {
        this.imageProcessor = imageProcessor;
    }

    public Image apply(InputStream input)
    {
        try {
            return imageProcessor.readImage(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
