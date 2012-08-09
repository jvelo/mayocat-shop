package org.mayocat.shop.rest.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.mayocat.shop.authorization.PasswordManager;
import org.mayocat.shop.authorization.cookies.CookieCrypter;
import org.mayocat.shop.authorization.cookies.EncryptionException;
import org.mayocat.shop.model.User;
import org.mayocat.shop.service.UserService;
import org.mayocat.shop.store.StoreException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

@Component("LoginResource")
@Path("/login/")
public class LoginResource implements Resource
{

    @Inject
    private UserService userService;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private CookieCrypter crypter;

    @GET
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@QueryParam("username") String username, @QueryParam("password") String password,
        @QueryParam("remember") @DefaultValue("false") Boolean remember, @Context UriInfo uri)
    {
        try {
            User user = userService.findByEmailOrUserName(username);

            if (user == null) {
                // Don't give more information than this
                return Response.status(Status.UNAUTHORIZED).build();
            }

            if (!passwordManager.verifyPassword(password, user.getPassword())) {
                // Don't give more information than this
                return Response.status(Status.UNAUTHORIZED).build();
            }

            // Find out some cookie parameters we will need
            int ageWhenRemember = 60 * 60 * 24 * 15; // TODO make configurable
            // String domain = uri.geBaseUri().getHost();
            // TODO set domain when at least two dots ? Or config ?
            // See http://curl.haxx.se/rfc/cookie_spec.html

            // Create the new cookies to be sent with the response
            NewCookie newUserCookie =
                new NewCookie("username", crypter.encrypt(username), "/", null, null, remember ? ageWhenRemember : -1,
                    false);
            NewCookie newPassCookie =
                new NewCookie("password", crypter.encrypt(password), "/", null, null, remember ? ageWhenRemember : -1,
                    false);

            return Response.ok().cookie(newUserCookie, newPassCookie).build();

        } catch (StoreException e) {
            throw new WebApplicationException(e);
        } catch (EncryptionException e) {
            // Don't give more information than this
            throw new WebApplicationException();
        }
    }
}
