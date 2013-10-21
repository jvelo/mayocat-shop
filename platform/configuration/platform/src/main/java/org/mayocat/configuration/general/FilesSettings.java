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
