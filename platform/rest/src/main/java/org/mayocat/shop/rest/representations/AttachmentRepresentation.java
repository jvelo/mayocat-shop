package org.mayocat.shop.rest.representations;

import org.mayocat.image.model.Image;
import org.mayocat.model.Attachment;
import org.mayocat.shop.rest.representations.EntityReferenceRepresentation;

/**
 * @version $Id$
 */
public class AttachmentRepresentation extends EntityReferenceRepresentation
{
    private FileRepresentation file;

    public AttachmentRepresentation(Attachment attachment)
    {
        super(attachment.getTitle(), buildAttachmentApiHref(attachment));
        this.file = buildFileRepresentation(attachment);
    }

    /**
     * Constructor that allows to override the attachment URI and its file representation.
     *
     * Particularly useful for extending classes, such as {@link ImageRepresentation}.
     *
     * @param attachment the attachment to represent.
     * @param uri the URI of the resource represented by the attachment representation
     * @param file the file representation of the attachment representation
     */
    public AttachmentRepresentation(Attachment attachment, String uri, FileRepresentation file)
    {
        super(attachment.getTitle(), uri);
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

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static FileRepresentation buildFileRepresentation(Attachment attachment)
    {
        return new FileRepresentation(attachment, buildImageFileHref(attachment));
    }

    private static String buildAttachmentApiHref(Attachment attachment)
    {
        return "/api/1.0/attachment/" + attachment.getSlug();
    }

    private static String buildImageFileHref(Attachment attachment)
    {
        return "/attachment/" + attachment.getSlug() + "." + attachment.getExtension();
    }
}
