package org.mayocat.shop.front.context;

import java.util.HashMap;

/**
 * @version $Id$
 */
public class ImageContext extends HashMap
{
    public ImageContext(String url)
    {
        this.setUrl(url);
    }

    public void setUrl(String url)
    {
        this.put("url", url);
    }

    public void setTitle(String title)
    {
        this.put("title", title);
    }

    public void setDescription(String description)
    {
        this.put("description", description);
    }
}
