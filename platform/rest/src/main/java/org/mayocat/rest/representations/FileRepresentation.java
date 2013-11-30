/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.representations;

import org.mayocat.model.Attachment;

/**
 * @version $Id$
 */
public class FileRepresentation
{
    private String extension;

    /**
     * "Public" URI at which this file contents is served. This is not an API URI, but a frontal, public facing URI.
     */
    private String href;

    public FileRepresentation()
    {
        // No-arg constructor required for Jackson deserialization
    }

    public FileRepresentation(Attachment attachment, String href)
    {
        this.extension = attachment.getExtension();
        this.href = href;
    }

    public FileRepresentation(String extension, String href)
    {
        this.extension = extension;
        this.href = href;
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public String getHref()
    {
        return href;
    }

    public void setHref(String href)
    {
        this.href = href;
    }
}
