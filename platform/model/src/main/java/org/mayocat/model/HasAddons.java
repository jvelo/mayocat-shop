package org.mayocat.model;

import java.util.List;

/**
 * @version $Id$
 */
public interface HasAddons
{
    List<Addon> getAddons();

    boolean conveyAddons();
}
