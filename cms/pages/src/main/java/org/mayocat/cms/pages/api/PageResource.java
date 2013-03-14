package org.mayocat.cms.pages.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mayocat.base.Resource;
import org.mayocat.shop.rest.AbstractAttachmentResource;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("/api/1.0/page/")
@Path("/api/1.0/page/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ExistingTenant
public class PageResource extends AbstractAttachmentResource implements Resource
{



}
