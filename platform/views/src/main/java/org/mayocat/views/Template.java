package org.mayocat.views;

/**
 * @version $Id$
 */
public class Template
{
    private String content;

    private String id;

    private boolean partial;

    public Template(String id, String content)
    {
        this(id, content, false);
    }

    public Template(String id, String content, boolean partial)
    {
        this.content = content;
        this.id = id;
        this.partial = partial;
    }

    public String getContent()
    {
        return content;
    }

    public String getId()
    {
        return id;
    }

    public boolean isPartial()
    {
        return partial;
    }
}
