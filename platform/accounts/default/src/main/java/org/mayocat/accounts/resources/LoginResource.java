package org.mayocat.accounts.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.mayocat.security.PasswordManager;
import org.mayocat.security.Cipher;
import org.mayocat.security.EncryptionException;
import org.mayocat.accounts.model.User;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.Resource;
import org.mayocat.accounts.AccountsService;
import org.xwiki.component.annotation.Component;

@Component(LoginResource.PATH)
@Path(LoginResource.PATH)
public class LoginResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "login";

    @Inject
    private AccountsService accountsService;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private Cipher crypter;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@FormParam("username") String username, @FormParam("password") String password,
        @FormParam("remember") @DefaultValue("false") Boolean remember)
    {
        try {
            User user = accountsService.findUserByEmailOrUserName(username);

            if (user == null) {
                // Don't give more information than this
                return Response.noContent().status(Status.UNAUTHORIZED).build();
            }

            if (!passwordManager.verifyPassword(password, user.getPassword())) {
                // Don't give more information than this
                return Response.noContent().status(Status.UNAUTHORIZED).build();
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

        } catch (EncryptionException e) {
            // Don't give more information than this
            return Response.status(Status.INTERNAL_SERVER_ERROR)
                    .entity("Failed to log in.").type(MediaType.TEXT_PLAIN_TYPE).build();
        }
    }
}
