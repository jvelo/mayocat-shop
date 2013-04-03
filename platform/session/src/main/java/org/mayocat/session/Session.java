package org.mayocat.session;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @version $Id$
 */
public interface Session extends Serializable
{
    boolean isEmpty();

    Set<String> getAttributeNames();

    Object getAttribute(String string);

    void setAttribute(String key, Serializable value);
}
