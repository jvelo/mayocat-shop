/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.configuration.general;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class FilesSettings
{
    @JsonProperty
    private Path permanentDirectory = Paths.get("data");

    @JsonProperty
    private Path temporaryDirectory = Paths.get("tmp");

    public Path getPermanentDirectory()
    {
        return permanentDirectory;
    }

    public Path getTemporaryDirectory()
    {
        return temporaryDirectory;
    }
}
