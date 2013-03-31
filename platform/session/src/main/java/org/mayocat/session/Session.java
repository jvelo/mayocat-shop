package org.mayocat.session;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * @version $Id$
 */
public interface Session extends Serializable
{
    Set<String> getAttributeNames();

    Object getAttribute(String string);

    void setAttribute(String key, Object value);
}
