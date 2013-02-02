package org.mayocat.shop;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface Slugifier
{
    String slugify(String toSlugify);
}
