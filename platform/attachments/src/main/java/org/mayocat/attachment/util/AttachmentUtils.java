package org.mayocat.attachment.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @version $Id$
 */
public class AttachmentUtils
{
    protected final static Set<String> IMAGE_EXTENSIONS = new HashSet<String>();

    static {
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("png");
        IMAGE_EXTENSIONS.add("gif");
    }

    public static boolean isImage(String fileName)
    {
        for (String extension : IMAGE_EXTENSIONS) {
            if (fileName.endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }
}
