/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.contact.handlebars;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Named;

import org.mayocat.views.rhino.handlebars.HelpersScript;
import org.xwiki.component.annotation.Component;

/**
 * Declares Handlebar.js helpers for the CMS contact module
 *
 * @version $Id$
 */
@Component
@Named("cmsContacts.js")
public class ContactHelpersScript implements HelpersScript
{
    @Override
    public Path getPath()
    {
        return Paths.get("javascripts/handlebars/helpers/cms/contact.js");
    }
}
