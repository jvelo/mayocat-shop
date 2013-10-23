package org.mayocat.session;

/**
 * A flash session is a session than spans only two HTTP requests. It is particularly useful to transport success/error
 * information accross a HTTP redirect.
 *
 * @version $Id$
 */
public interface Flash extends WebScope
{
    boolean isConsumed();
}
