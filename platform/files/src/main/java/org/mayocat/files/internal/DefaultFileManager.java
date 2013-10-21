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
