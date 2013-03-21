package org.mayocat.cms.news.front.resource;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.mayocat.base.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.front.resources.AbstractFrontResource;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("/news/")
@Path("/news/")
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class NewsResource extends AbstractFrontResource implements Resource
{
}
