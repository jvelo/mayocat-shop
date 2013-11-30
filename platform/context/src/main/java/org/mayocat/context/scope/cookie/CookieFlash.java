/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.context.scope.cookie;

import org.mayocat.context.scope.Flash;

/**
 * Cookie based implementation of the {@link org.mayocat.context.scope.Flash} session.
 *
 * @version $Id$
 */
public class CookieFlash extends CookieSession implements Flash
{
    private boolean isConsumed = false;

    public void consume()
    {
        isConsumed = true;
    }

    public boolean isConsumed()
    {
        return isConsumed;
    }
}
