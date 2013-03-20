package org.mayocat.configuration.general;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @version $Id$
 */
public class FilesSettings
{
    @JsonProperty
    private String permanentDirectory = "data";

    @JsonProperty
    private String temporaryDirectory = "tmp";

    public String getPermanentDirectory()
    {
        return permanentDirectory;
    }

    public String getTemporaryDirectory()
    {
        return temporaryDirectory;
    }
}
