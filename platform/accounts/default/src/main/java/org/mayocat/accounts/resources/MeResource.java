package org.mayocat.accounts.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.joda.time.DateTimeZone;
import org.mayocat.accounts.AccountsService;
import org.mayocat.accounts.model.User;
import org.mayocat.accounts.representations.TenantRepresentation;
import org.mayocat.accounts.representations.UserAndTenantRepresentation;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.Context;
import org.mayocat.context.Execution;
import org.mayocat.rest.Resource;
import org.mayocat.security.Cipher;
import org.mayocat.security.EncryptionException;
import org.mayocat.security.PasswordManager;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component(MeResource.PATH)
@Path(MeResource.PATH)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Authorized
public class MeResource implements Resource
{
    public static final String PATH = API_ROOT_PATH + "me";

    public final static class ChangePasswordRequest
    {
        private final String currentPassword;

        private final String newPassword;

        public ChangePasswordRequest(String currentPassword, String newPassword)
        {
            this.currentPassword = currentPassword;
            this.newPassword = newPassword;
        }

        public String getCurrentPassword()
        {
            return currentPassword;
        }

        public String getNewPassword()
        {
            return newPassword;
        }
    }

    @Inject
    private AccountsService accountsService;

    @Inject
    private Execution execution;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private PasswordManager passwordManager;

    @Inject
    private Cipher crypter;

    @Inject
    private Logger logger;

    @GET
    public UserAndTenantRepresentation getCurrentUser()
    {
        UserAndTenantRepresentation userAndTenant = new UserAndTenantRepresentation();

        if (this.execution.getContext().getTenant() != null) {
            userAndTenant
                    .setTenant(new TenantRepresentation(getGlobalTimeZone(), this.execution.getContext().getTenant()));
        }
        Context context = execution.getContext();
        userAndTenant.setUser(context.getUser());

        return userAndTenant;
    }

    @POST
    public Response changeMyPassword(@CookieParam("remember") Boolean remember, ChangePasswordRequest request)
    {
        User user = execution.getContext().getUser();

        if (!passwordManager.verifyPassword(request.getCurrentPassword(), user.getPassword())) {
            return Response.noContent().status(Response.Status.UNAUTHORIZED).build();
        }

        user.setPassword(this.passwordManager.hashPassword(request.getNewPassword()));

        try {
            accountsService.updateUser(user);

            // Update cookies so that the user doesn't have to log in again

            // Find out some cookie parameters we will need
            int ageWhenRemember = 60 * 60 * 24 * 15; // TODO make configurable
            // String domain = uri.geBaseUri().getHost();
            // TODO set domain when at least two dots ? Or config ?
            // See http://curl.haxx.se/rfc/cookie_spec.html

            // Create the new cookies to be sent with the response
            NewCookie newUserCookie = new NewCookie("username", crypter.encrypt(user.getSlug()), "/", null, null,
                    remember ? ageWhenRemember : -1, false);
            NewCookie newPassCookie = new NewCookie("password", crypter.encrypt(user.getPassword()), "/", null, null,
                    remember ? ageWhenRemember : -1, true);
            NewCookie newRememberMe = new NewCookie("remember", remember.toString(), "/", null, null,
                    remember ? ageWhenRemember : -1, false);

            return Response.noContent().cookie(newUserCookie, newPassCookie, newRememberMe).build();

        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build();
        } catch (EncryptionException e) {
            logger.error("Encryption exception when attempting to change password", e);
            return Response.serverError().build();
        }
    }

    private DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue());
    }
}
