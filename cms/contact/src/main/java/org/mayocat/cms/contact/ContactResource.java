/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.cms.contact;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.mayocat.context.WebContext;
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

    private static final String PARAMETER_REDIRECT_TO = "redirectTo";

    private static final String PARAMETER_SUBJECT = "subject";

    private static final String DEFAULT_SUBJECT = "New contact message";

    @Inject
    private MailService mailService;

    @Inject
    private WebContext context;

    @POST
    public Response postContactMessage(MultivaluedMap<String, String> form)
    {
        String redirectTo = form.getFirst(PARAMETER_REDIRECT_TO);

        if (Strings.isNullOrEmpty(redirectTo)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        String subject = StringUtils.defaultIfBlank(form.getFirst(PARAMETER_SUBJECT), DEFAULT_SUBJECT);

        String text = "";

        for (String name : form.keySet()) {
            if (!Arrays.asList(PARAMETER_REDIRECT_TO, PARAMETER_SUBJECT).contains(name)) {
                text += (name.toUpperCase() + ": " + form.getFirst(name) + "\n");
            }
        }

        Mail mail = mailService.emailToTenant().subject(subject).text(text);

        try {
            URI returnTo = new URI(redirectTo);
            try {
                mailService.sendEmail(mail);
                context.flash("postContactMessage", "Success");
                return Response.seeOther(returnTo).build();
            } catch (MailException e) {
                context.flash("postContactMessage", "Failure");
                return Response.seeOther(returnTo).build();
            }
        } catch (URISyntaxException e) {
            return Response.serverError().build();
        }
    }
}
