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
}
