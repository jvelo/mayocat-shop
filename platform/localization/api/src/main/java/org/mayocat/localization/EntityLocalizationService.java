/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.localization;

import java.util.Locale;

import org.mayocat.model.Localized;
import org.xwiki.component.annotation.Role;

/**
 * Service that localizes {@link Localized} entities : it takes the original entity (with contents written in the
 * tenant defined main language) and returns it "translated" in a target language, if the translations exists.
 * When some of the translated entity text exists in the target language but not all of them, the service can return
 * a partially translated only entity : only the fields and addons for which a translation exist will be translated,
 * and the rest will remain in the original language.
 *
 * @version $Id$
 */
@Role
public interface EntityLocalizationService
{
    /**
     * Localize an entity in the request context's language (for example the language the user is visiting the web site
     * in).
     */
    <T extends Localized> T localize(T entity);

    /**
     * Localize an entity using the passed locale as target language.
     */
    <T extends Localized> T localize(T entity, Locale locale);
}
