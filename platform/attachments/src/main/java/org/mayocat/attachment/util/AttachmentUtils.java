/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment.util;

import java.util.HashSet;
import java.util.Set;

import org.mayocat.model.Attachment;

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

    public static boolean isImage(Attachment attachment) {
        return isImage(attachment.getFilename());
    }

    public static boolean isImage(String fileName)
    {
        for (String extension : IMAGE_EXTENSIONS) {
            if (fileName.toLowerCase().endsWith("." + extension)) {
                return true;
            }
        }
        return false;
    }
}
