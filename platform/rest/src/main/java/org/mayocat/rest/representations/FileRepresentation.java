package org.mayocat.rest.representations;

import org.mayocat.model.Attachment;

/**
 * @version $Id$
 */
public class FileRepresentation
{
    private String extension;

    /**
     * "Public" URI at which this file contents is served. This is not an API URI, but a frontal, public facing URI.
     */
    private String href;

    public FileRepresentation(Attachment attachment, String href)
    {
        this.extension = attachment.getExtension();
        this.href = href;
    }

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
