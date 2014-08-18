/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment.model;

/**
 * @version $Id$
 */
public class LoadedAttachment extends Attachment
{
    public AttachmentData getData()
    {
        return data;
    }

    public void setData(AttachmentData data)
    {
        this.data = data;
    }

    private AttachmentData data;
}
