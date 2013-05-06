package org.mayocat.shop.front.context;

import java.util.HashMap;

/**
 * @version $Id$
 */
public class ImageContext extends HashMap
{

    private String url;

    private String title;

    private String description;

    public ImageContext(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
