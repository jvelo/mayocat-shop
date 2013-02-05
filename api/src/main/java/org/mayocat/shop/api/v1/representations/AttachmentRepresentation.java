package org.mayocat.shop.api.v1.representations;

import org.mayocat.shop.rest.representations.EntityReferenceRepresentation;

/**
 * @version $Id$
 */
public class AttachmentRepresentation extends EntityReferenceRepresentation
{
    private FileRepresentation file;

    public AttachmentRepresentation(String title, String uri, FileRepresentation file)
    {
        super(uri, title);
        this.file = file;
    }

    public FileRepresentation getFile()
    {
        return file;
    }

    public void setFile(FileRepresentation file)
    {
        this.file = file;
    }
}
