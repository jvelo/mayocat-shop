package org.mayocat.shop.api.v1.representations;

import java.util.List;

/**
 * @version $Id$
 */
public class ImageRepresentation extends AttachmentRepresentation
{
    private List<ThumbnailRepresentation> thumbnails;

    public ImageRepresentation(String title, String href,
            FileRepresentation file, List<ThumbnailRepresentation> thumbnails)
    {
        super(title, href, file);

        this.thumbnails = thumbnails;
    }

    public List<ThumbnailRepresentation> getThumbnails()
    {
        return thumbnails;
    }
}
