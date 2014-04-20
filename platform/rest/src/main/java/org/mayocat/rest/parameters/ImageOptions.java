/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.parameters;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class ImageOptions
{
    private Optional<Integer> width;

    private Optional<Integer> height;

    public ImageOptions(Optional<Integer> width, Optional<Integer> height)
    {
        this.width = width;
        this.height = height;
    }

    public Optional<Integer> getWidth()
    {
        return width;
    }

    public Optional<Integer> getHeight()
    {
        return height;
    }
}
