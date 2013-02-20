package org.mayocat.shop.front;

import java.util.List;
import java.util.Map;

import javax.inject.Singleton;
import javax.ws.rs.core.PathSegment;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface FrontBindingManager
{

    Map<String, Object> getBindings(List<PathSegment> segments);
}
