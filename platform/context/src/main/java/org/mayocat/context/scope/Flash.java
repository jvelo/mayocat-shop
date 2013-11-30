/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context.scope;

/**
 * A flash session is a session than spans only two HTTP requests. It is particularly useful to transport success/error
 * information across a HTTP redirect.
 *
 * @version $Id$
 */
public interface Flash extends WebScope
{
    boolean isConsumed();
}
