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
