/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
