/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.templating.jtwig;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

import com.lyncode.jtwig.exception.ResourceException;
import com.lyncode.jtwig.resource.JtwigResource;

/**
 * @version $Id$
 */
public class JailedPathJtwigResource implements JtwigResource
{
    private Path jail;

    private Path resourcePath;

    public JailedPathJtwigResource(Path jail, Path resourcePath)
    {
        this.jail = jail.normalize();
        this.resourcePath = resourcePath.normalize();

        if (!this.resourcePath.toAbsolutePath().startsWith(this.jail.toAbsolutePath())) {
            throw new IllegalArgumentException("Trying to access a file outside the jail");
        }
    }

    @Override
    public InputStream retrieve() throws ResourceException
    {
        try {
            return new FileInputStream(resourcePath.toFile());
        } catch (FileNotFoundException e) {
            throw new ResourceException(e);
        }
    }

    @Override
    public JtwigResource resolve(String relativePath) throws ResourceException
    {
        return new JailedPathJtwigResource(jail, resourcePath.getParent().resolve(relativePath));
    }
}
