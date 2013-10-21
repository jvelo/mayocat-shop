package org.mayocat.files;

import java.nio.file.Path;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface FileManager
{
    Path resolvePermanentFilePath(Path path);
}
