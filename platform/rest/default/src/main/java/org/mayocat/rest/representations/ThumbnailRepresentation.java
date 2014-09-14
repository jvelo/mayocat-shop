/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.representations;

import javax.validation.constraints.NotNull;

import org.mayocat.image.model.Thumbnail;

/**
 * @version $Id$
 */
public class ThumbnailRepresentation
{
    @NotNull
    private String source;

    @NotNull
    private String hint;

    private String ratio;

    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    @NotNull
    private Integer width;

    @NotNull
    private Integer height;

    public ThumbnailRepresentation()
    {

    }

    public ThumbnailRepresentation(Thumbnail thumbnail)
    {
        setSource(thumbnail.getSource());
        setHint(thumbnail.getHint());
        setRatio(thumbnail.getRatio());
        setX(thumbnail.getX());
        setY(thumbnail.getY());
        setWidth(thumbnail.getWidth());
        setHeight(thumbnail.getHeight());
    }

    public String getSource()
    {
        return source;
    }

    public void setSource(String source)
    {
        this.source = source;
    }

    public String getHint()
    {
        return hint;
    }

    public void setHint(String hint)
    {
        this.hint = hint;
    }

    public String getRatio()
    {
        return ratio;
    }

    public void setRatio(String ratio)
    {
        this.ratio = ratio;
    }

    public Integer getX()
    {
        return x;
    }

    public void setX(Integer x)
    {
        this.x = x;
    }

    public Integer getY()
    {
        return y;
    }

    public void setY(Integer y)
    {
        this.y = y;
    }

    public Integer getWidth()
    {
        return width;
    }

    public void setWidth(Integer width)
    {
        this.width = width;
    }

    public Integer getHeight()
    {
        return height;
    }

    public void setHeight(Integer height)
    {
        this.height = height;
    }
}
