/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.model;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

public interface Localized extends Entity, Serializable
{
    Map<Locale, Map<String, Object>> getLocalizedVersions();
}
