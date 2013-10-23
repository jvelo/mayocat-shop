package org.mayocat.cms.contact;

import java.net.URI;
import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.mayocat.context.Execution;
import org.mayocat.mail.Mail;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.front.context.ContextConstants;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

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

    @Inject
    private Execution execution;

    @POST
    public Response postContactMessage(MultivaluedMap<String, String> form)
    {
        String sentFrom = form.getFirst("sentFrom");

        if (Strings.isNullOrEmpty(sentFrom)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // TODO
        Mail mail = new Mail().from("toto@example.fr").to("jerome@velociter.fr");

        try {
            URI returnTo = new URI(sentFrom);
            try {
                mailService.sendEmail(mail);
                execution.getContext().flash("postContactMessage", "Success");
                return Response.seeOther(returnTo).build();
            } catch (MailException e) {
                execution.getContext().flash("postContactMessage", "Failure");
                return Response.seeOther(returnTo).build();
            }
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }
}
