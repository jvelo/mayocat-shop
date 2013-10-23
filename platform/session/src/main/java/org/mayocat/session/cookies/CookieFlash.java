package org.mayocat.session.cookies;

import org.mayocat.session.Flash;

/**
 * Cookie based implementation of the {@link Flash} session.
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
