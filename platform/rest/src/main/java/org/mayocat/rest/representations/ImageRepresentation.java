package org.mayocat.rest.representations;

import java.util.List;

import org.mayocat.image.model.Image;
import org.mayocat.image.model.Thumbnail;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class ImageRepresentation extends AttachmentRepresentation
{
    private List<ThumbnailRepresentation> thumbnails;

    public ImageRepresentation(Image image)
    {
        super(image.getAttachment(), buildImageApiHref(image), buildFileRepresentation(image));
        this.thumbnails = buildThumbnailsRepresentation(image);
    }

    public List<ThumbnailRepresentation> getThumbnails()
    {
        return thumbnails;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static String buildImageApiHref(Image image)
    {
        return "/api/1.0/image/" + image.getAttachment().getSlug();
    }

    private static String buildImageFileHref(Image image)
    {
        return "/image/" + image.getAttachment().getSlug() + "." + image.getAttachment().getExtension();
    }

    private static FileRepresentation buildFileRepresentation(Image image)
    {
        return new FileRepresentation(image.getAttachment(), buildImageFileHref(image));
    }

    private static List<ThumbnailRepresentation> buildThumbnailsRepresentation(Image image)
    {
        List<ThumbnailRepresentation> thumbnailRepresentations = Lists.newArrayList();
        for (Thumbnail thumb : image.getThumbnails()) {
            thumbnailRepresentations.add(new ThumbnailRepresentation(thumb));
        }
        return thumbnailRepresentations;
    }
}
