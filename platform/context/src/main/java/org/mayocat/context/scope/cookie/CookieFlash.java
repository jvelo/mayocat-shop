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
