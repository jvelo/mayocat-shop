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
