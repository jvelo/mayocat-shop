package org.mayocat.shop.front;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.rest.views.FrontView;
import org.mayocat.shop.theme.Breakpoint;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface EntityFrontViewBuilder
{
    <E extends Entity> FrontView buildFrontView(String layout, E entity, Breakpoint breakpoint);

    <E extends Entity> FrontView buildFrontView(E entity);

    <E extends Entity> FrontView buildFrontView(E entity, Breakpoint breakpoint);

    FrontView buildFrontView(String layout, Breakpoint breakpoint);

    FrontView build404(Breakpoint breakpoint);
}
