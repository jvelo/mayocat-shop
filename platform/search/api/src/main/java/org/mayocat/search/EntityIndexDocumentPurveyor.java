/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search;

import java.util.Map;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.model.Entity;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityIndexDocumentPurveyor<E extends Entity>
{
    Map<String, Object> purveyDocument(E entity);

    Map<String, Object> purveyDocument(E entity, Tenant tenant);
}
