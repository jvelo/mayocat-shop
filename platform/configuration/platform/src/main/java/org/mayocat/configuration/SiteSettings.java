/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
    private String domainName = "localhost:8080";

    public String getDomainName()
    {
        return domainName;
    }
}
