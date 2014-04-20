/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.representations;

import java.util.Locale;
import java.util.Map;

import org.mayocat.model.Attachment;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @version $Id$
 */
public class AttachmentRepresentation extends EntityReferenceRepresentation
{
    private FileRepresentation file;

    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<Locale, Map<String, Object>> localizedVersions = null;

    public AttachmentRepresentation()
    {
        // No-arg constructor required for Jackson deserialization
        super();
    }

    public AttachmentRepresentation(Attachment attachment)
    {
        super(buildAttachmentApiHref(attachment), attachment.getSlug(), attachment.getTitle());
        this.file = buildFileRepresentation(attachment);
        this.description = attachment.getDescription();
        this.localizedVersions = attachment.getLocalizedVersions();
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
        super(uri, attachment.getSlug(), attachment.getTitle());
        this.file = file;
        this.description = attachment.getDescription();
        this.localizedVersions = attachment.getLocalizedVersions();
    }

    public FileRepresentation getFile()
    {
        return file;
    }

    public void setFile(FileRepresentation file)
    {
        this.file = file;
    }

    public String getDescription()
    {
        return description;
    }

    public Map<Locale, Map<String, Object>> getLocalizedVersions()
    {
        return localizedVersions;
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
