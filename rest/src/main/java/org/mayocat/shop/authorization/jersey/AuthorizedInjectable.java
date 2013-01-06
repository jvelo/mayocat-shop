package org.mayocat.shop.authorization.jersey;

import java.util.Arrays;
import java.util.Map;

import javax.inject.Provider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.Authenticator;
import org.mayocat.shop.authorization.Capability;
import org.mayocat.shop.authorization.Gatekeeper;
import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.authorization.capability.shop.AddUser;
import org.mayocat.shop.context.Context;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.multitenancy.TenantResolver;
import org.mayocat.shop.service.UserService;
import org.mayocat.shop.store.StoreException;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.HttpContext;

class AuthorizedInjectable extends AnonymousInjectable
{
    private boolean optional = false;

    private Map<String, Authenticator> authenticators;

    private Class< ? extends Capability>[] capabilities;

    private Gatekeeper gatekeeper;

    private UserService userService;
    
    AuthorizedInjectable(UserService userService, Provider<TenantResolver> provider,
        Map<String, Authenticator> authenticators, Gatekeeper gatekeeper, Authorized auth, Execution execution)
    {
        super(provider, execution);

        this.authenticators = authenticators;
        this.gatekeeper = gatekeeper;
        this.userService = userService;

        this.optional = auth.optional();
        this.capabilities = auth.value();
    }

    @Override
    public Context getValue(HttpContext httpContext)
    {
        // Not calling super.getValue() here to get the tenant as it results in calling the
        // AbstractHttpContextInjectable#getValue()
        // not AnonymousInjectable
        Context context;
        Tenant tenant = this.provider.get().resolve(httpContext.getUriInfo().getBaseUri().getHost());
        if (tenant == null) {

            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                .entity("No valid tenant found at this address.").type(MediaType.TEXT_PLAIN_TYPE).build());

            // NOTE
            // At some point we will want to allow certain actions in a "global" context (i.e. not bound to a particular
            // tenant)
            // For example:
            // - browsing anonymously a marketplace at the root domain
            // - managing tenants as a "farm admin"
        }

        Optional<User> user = Optional.absent();
        for (String headerName : Lists.newArrayList("Authorization", "Cookie")) {
            final String headerValue = Strings.nullToEmpty(httpContext.getRequest().getHeaderValue(headerName));
            for (Authenticator authenticator : this.authenticators.values()) {
                if (authenticator.respondTo(headerName, headerValue)) {
                    user = authenticator.verify(headerValue, tenant);
                    if (user.isPresent()) {
                        if (capabilities.length == 0) {
                            // No capability declared, just return authenticated user
                            context = new Context(tenant, user.get());
                            this.execution.setContext(context);
                            return context;
                        }
                        // We have a valid user... now verify capabilities
                        boolean hasAllRequiredCapabilities = true;
                        for (Class< ? extends Capability> capability : Arrays.asList(this.capabilities)) {
                            hasAllRequiredCapabilities &= this.gatekeeper.hasCapability(user.get(), capability);
                        }
                        if (hasAllRequiredCapabilities) {
                            context = new Context(tenant, user.get());
                            this.execution.setContext(context);
                            return context;
                        }
                    }
                }
            }
        }

        if (this.isTenantEmptyOfUser() && this.capabilityIsExactlyAddUser()) {

            // Handle the special case where the tenant is empty of users (just created for example).
            // In this case and when the capability asked is exactly and only "create user", we let go through
            // to allow the creation of the initial admin user.
            
            // Let go
        }
        else {
        
            // If some conditions are not met... FIRE (╯°Д°）╯︵ /(.□ . \)
        
            if (!this.optional && !user.isPresent()) {

                // Not authenticated

                throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "realm=\"Mayocat Shop\"")
                    // FIXME Let first responding authenticator provide its challenge

                    .entity("Request is not properly authenticated.").type(MediaType.TEXT_PLAIN_TYPE).build());
            } else if (!this.optional) {

                // Forbidden

                throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                    .entity("Insufficient privileges").type(MediaType.TEXT_PLAIN_TYPE).build());
            }
        }
        context = new Context(tenant, user.orNull());
        this.execution.setContext(context);
        return context;
    }

    private boolean isTenantEmptyOfUser()
    {
        try {
            return !this.userService.hasUsers();
        } catch (StoreException e) {
            // Assume the worst
            return false;
        }
    }
    
    private boolean capabilityIsExactlyAddUser()
    {
        return this.capabilities.length == 1 && this.capabilities[0].equals(AddUser.class);
    }

}
