package org.mayocat.image.model;

import java.util.List;

import org.mayocat.model.Attachment;


/**
 * @version $Id$
 */
public class Image
{
    private Attachment attachment;

    private List<Thumbnail> thumbnails;

    public Image(org.mayocat.model.Attachment attachment, List<Thumbnail> thumbnails)
    {
        this.attachment = attachment;
        this.thumbnails = thumbnails;
    }

    public Attachment getAttachment()
    {
        return attachment;
    }

    public List<Thumbnail> getThumbnails()
    {
        return thumbnails;
    }
}
