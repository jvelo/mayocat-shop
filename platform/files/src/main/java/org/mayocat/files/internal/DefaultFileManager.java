/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.files.internal;

import java.nio.file.Path;

import javax.inject.Inject;

import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.files.FileManager;

/**
 * @version $Id$
 */
public class DefaultFileManager implements FileManager
{

    @Inject
    private FilesSettings filesSettings;

    @Override
    public Path resolvePermanentFilePath(Path path)
    {
        return filesSettings.getPermanentDirectory().resolve(path);
    }
}
