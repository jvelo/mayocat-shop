/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.views;

/**
 * @version $Id$
 */
public class Template
{
    private String content;

    private String id;

    private boolean partial;

    public Template(String id, String content)
    {
        this(id, content, false);
    }

    public Template(String id, String content, boolean partial)
    {
        this.content = content;
        this.id = id;
        this.partial = partial;
    }

    public String getContent()
    {
        return content;
    }

    public String getId()
    {
        return id;
    }

    public boolean isPartial()
    {
        return partial;
    }
}
