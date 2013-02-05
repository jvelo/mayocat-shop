package org.mayocat.shop.api.v1.reprensentations;

/**
 * @version $Id$
 */
public class FileRepresentation
{
    private String extension;

    private String href;

    public FileRepresentation(String extension, String href)
    {
        this.extension = extension;
        this.href = href;
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }
}
