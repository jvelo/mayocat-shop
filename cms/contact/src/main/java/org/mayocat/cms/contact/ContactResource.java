package org.mayocat.cms.contact;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.mail.Mail;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.front.context.ContextConstants;
import org.xwiki.component.annotation.Component;

/**
 * API end-point for the "Contact Us" forms.
 *
 * @version $Id$
 */
@Component(ContactResource.PATH)
@Path(ContactResource.PATH)
@Produces(MediaType.TEXT_HTML)
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class ContactResource implements Resource, ContextConstants
{
    public static final String PATH = ROOT_PATH + "contact";

    @Inject
    private MailService mailService;

    @POST
    public Response postContactMessage()
    {
        // TODO
        Mail mail = new Mail();

        try {
            mailService.sendEmail(mail);

            return Response.noContent().build();
        } catch (MailException e) {
            return Response.serverError().build();
        }
    }
}
