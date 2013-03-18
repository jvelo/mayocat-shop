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
