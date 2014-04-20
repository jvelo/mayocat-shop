/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context.scope;

import java.io.Serializable;
import java.util.Set;

/**
 * Represents a context scope (for example: session, or flash).
 *
 * @version $Id$
 */
public interface WebScope extends Serializable
{
    boolean isEmpty();

    Set<String> getAttributeNames();

    Object getAttribute(String string);

    void setAttribute(String key, Serializable value);

    void removeAttribute(String key);
}
