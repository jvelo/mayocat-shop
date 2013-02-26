package org.mayocat.shop.views;

/**
 * @version $Id$
 */
public class Template
{
    private String content;

    private String name;

    private boolean partial;

    public Template(String name, String content)
    {
        this(name, content, false);
    }

    public Template(String name, String content, boolean partial)
    {
        this.content = content;
        this.name = name;
        this.partial = partial;
    }

    public String getContent()
    {
        return content;
    }

    public String getName()
    {
        return name;
    }

    public boolean isPartial()
    {
        return partial;
    }
}
