package org.mayocat.model;

import java.util.List;

/**
 * @version $Id$
 */
public interface HasAddons
{
    Association<List<Addon>> getAddons();

    void setAddons(List<Addon> addons);
}
