package org.mayocat.image.model;

import java.util.UUID;

/**
 * @version $Id$
 */
public class Thumbnail
{
    private String source;

    private String hint;

    private String ratio;

    private Integer x;

    private Integer y;

    private Integer width;

    private Integer height;

    private UUID attachmentId;

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

    public UUID getAttachmentId()
    {
        return attachmentId;
    }

    public void setAttachmentId(UUID attachmentId)
    {
        this.attachmentId = attachmentId;
    }
}
