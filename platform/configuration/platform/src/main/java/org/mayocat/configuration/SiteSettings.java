package org.mayocat.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @version $Id$
 */
public class SiteSettings
{
    /**
     * The domain this web site runs under. Here the term "domain" is a language abuse since it does not correspond
     * exactly to what an internet domain name is (for example here the default value contains a port specification).
     *
     * This is use for all absolute URL creation.
     */
    @Valid
    @NotNull
    private String domainName = "locahost:8080";

    public String getDomainName()
    {
        return domainName;
    }
}
