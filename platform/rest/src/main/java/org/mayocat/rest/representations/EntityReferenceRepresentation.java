/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.representations;

/**
 * @version $Id$
 */
public class EntityReferenceRepresentation extends LinkRepresentation
{
    private String title;

    private String slug;

    public EntityReferenceRepresentation()
    {
        // No-arg constructor required for Jackson deserialization
        super();
    }

    public EntityReferenceRepresentation(String href, String slug, String title)
    {
        super(href);
        this.title = title;
        this.slug = slug;
    }

    public String getTitle()
    {
        return title;
    }

    public String getSlug()
    {
        return slug;
    }
}
