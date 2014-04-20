/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.theme;

import java.nio.file.Path;

/**
 * @version $Id$
 */
public class ThemeResource
{
    public enum Type
    {
        CLASSPATH_RESOURCE,
        FILE
    }

    private Type type;

    private Path path;

    public ThemeResource(Type type, Path path)
    {
        this.type = type;
        this.path = path;
    }

    public Path getPath()
    {
        return this.path;
    }

    public Type getType()
    {
        return this.type;
    }
}
