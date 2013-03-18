package org.mayocat.model;

import java.util.List;

/**
 * @version $Id$
 */
public interface HasAddons
{
    PerhapsLoaded<List<Addon>> getAddons();
}
