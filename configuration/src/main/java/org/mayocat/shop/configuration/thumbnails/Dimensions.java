package org.mayocat.shop.configuration.thumbnails;

public class Dimensions
{
    private Integer width;

    private Integer height;

    public Dimensions(Integer width, Integer height)
    {
        this.width = width;
        this.height = height;
    }

    public Integer getWidth()
    {
        return width;
    }

    public Integer getHeight()
    {
        return height;
    }

    public void setWidth(Integer width)
    {
        this.width = width;
    }

    public void setHeight(Integer height)
    {
        this.height = height;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Dimensions that = (Dimensions) o;

        if (height != null ? !height.equals(that.height) : that.height != null) {
            return false;
        }
        if (width != null ? !width.equals(that.width) : that.width != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = width != null ? width.hashCode() : 0;
        result = 31 * result + (height != null ? height.hashCode() : 0);
        return result;
    }
}
