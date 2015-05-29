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
public class LocaleRepresentation
{
    private String tag;

    private String name;

    public LocaleRepresentation(String tag, String name)
    {
        this.tag = tag;
        this.name = name;
    }

    public String getTag()
    {
        return tag;
    }

    public String getName()
    {
        return name;
    }
}
