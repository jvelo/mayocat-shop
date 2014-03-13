package org.mayocat.shop.catalog.web

import org.mayocat.model.Attachment

/**
 * FIXME
 * Implement this as a trait if/when traits makes it to groovy.
 *
 * @version $Id$
 */
class AbstractWebView
{
    protected final static Set<String> IMAGE_EXTENSIONS = new HashSet<String>();

    static {
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("png");
        IMAGE_EXTENSIONS.add("gif");
    }

    public static boolean isImage(Attachment attachment)
    {
        return IMAGE_EXTENSIONS.contains(attachment.getExtension().toLowerCase());
    }
}
