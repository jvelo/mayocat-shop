package org.mayocat.shop.front.resources;

import java.util.HashSet;
import java.util.Set;

import org.mayocat.model.Attachment;

/**
 * @version $Id$
 */
public class AbstractFrontResource
{
    protected final static Set<String> IMAGE_EXTENSIONS = new HashSet<String>();

    static {
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("png");
        IMAGE_EXTENSIONS.add("gif");
    }

    protected static boolean isImage(Attachment attachment)
    {
        return IMAGE_EXTENSIONS.contains(attachment.getExtension().toLowerCase());
    }
}
