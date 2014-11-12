package org.mayocat.accounts.api.v1

import groovy.transform.CompileStatic
import org.mayocat.rest.Resource
import org.xwiki.component.annotation.Component

import javax.ws.rs.Consumes
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType

/**
 * @version $Id$
 */
@Component("/api/user")
@Path("/api/user")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class UserApi extends TenantUserApi implements Resource
{
}
