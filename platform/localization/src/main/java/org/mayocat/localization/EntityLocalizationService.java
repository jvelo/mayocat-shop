package org.mayocat.localization;

import java.util.Locale;

import org.mayocat.model.Localized;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityLocalizationService
{
    <T extends Localized> T localize(T entity);

    <T extends Localized> T localize(T entity, Locale locale);
}
