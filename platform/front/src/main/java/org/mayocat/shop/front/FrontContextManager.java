package org.mayocat.shop.front;

import java.util.Map;

import javax.ws.rs.core.UriInfo;

import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface FrontContextManager
{

    Map<String, Object> getContext(UriInfo uriInfo);
}
