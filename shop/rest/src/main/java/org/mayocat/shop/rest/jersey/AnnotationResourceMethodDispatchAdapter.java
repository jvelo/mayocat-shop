package org.mayocat.shop.rest.jersey;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.shop.authorization.Gatekeeper;
import org.mayocat.shop.authorization.annotation.Authorized;
import org.mayocat.shop.base.Provider;
import org.mayocat.shop.context.Execution;
import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.rest.annotation.ExistingTenant;
import org.mayocat.shop.service.AccountsService;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

/**
 * Processes resources and check for the presence of Mayocat-specific annotation on the resource method and/or class
 * and instrument the resource accordingly if necessary.
 *
 * @version $Id$
 */
@Component("annotationResourceMethodDispatchAdapter")
public class AnnotationResourceMethodDispatchAdapter implements ResourceMethodDispatchAdapter, Provider
{
    @Inject
    private Execution execution;

    @Inject
    private Gatekeeper gatekeeper;

    @Inject
    private AccountsService accountsService;

    /**
     * Request dispatcher that checks if a valid tenant has been set in the execution context,
     * throws a NOT FOUND (404) if none is found.
     * <p />
     * Used when {@link ExistingTenant} annotation is present on a resource.
     */
    private class CheckValidTenantRequestDispatcher implements RequestDispatcher {

        private final RequestDispatcher underlying;

        public CheckValidTenantRequestDispatcher(RequestDispatcher underlying)
        {
            this.underlying = underlying;
        }
        @Override
        public void dispatch(Object resource, HttpContext httpContext)
        {
            if (execution.getContext().getTenant() == null) {
                throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND)
                        .entity("No valid tenant found at this address.").type(MediaType.TEXT_PLAIN_TYPE).build());
            }
            underlying.dispatch(resource, httpContext);
        }
    }

    /**
     * Request dispatcher that checks the authorization level of the user set in the context relatively to
     * the declared clearance required by the {@link Authorized} annotation on a resource.
     */
    private class CheckAuthorizationMethodDispatcher implements RequestDispatcher {

        private final AbstractResourceMethod method;
        private final RequestDispatcher underlying;
        private final Authorized annotation;

        public CheckAuthorizationMethodDispatcher(AbstractResourceMethod method, Authorized authorizedAnnotation, RequestDispatcher underlying)
        {
            this.underlying = underlying;
            this.method = method;
            this.annotation = authorizedAnnotation;
        }

        @Override
        public void dispatch(Object resource, HttpContext httpContext)
        {
            User user = execution.getContext().getUser();

            if (user != null) {
                if (!this.checkAuthorization(user)) {
                    throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                            .entity("Insufficient privileges").type(MediaType.TEXT_PLAIN_TYPE).build());
                }
            }
            else {
                if (this.isTenantEmptyOfUser() && this.isCreateUserResource()) {
                    // This means there is no user for that tenant yet, and this is the request to create the
                    // initial user.
                    // Awright, you good to go

                } else {
                    // TODO make the authentication challenge configurable
                    throw new WebApplicationException(Response.status(Response.Status.UNAUTHORIZED)
                            .header("WWW-Authenticate", "realm=\"Mayocat Shop\"")
                            .entity("Request is not properly authenticated.")
                            .type(MediaType.TEXT_PLAIN_TYPE)
                            .build());
                }
            }

            underlying.dispatch(resource, httpContext);
        }

        // TODO Probably extract this to the authorization/gatekeeper module
        private boolean checkAuthorization(User user)
        {
            if (this.annotation.roles().length == 0 && user != null) {
                // No specific role is required, just an authenticated user
                return true;
            }

            List<Role> roles = accountsService.findRolesForUser(user);
            for (Role role : roles) {
                if (Arrays.asList(this.annotation.roles()).contains(role)) {
                    return true;
                }
            }

            return false;
        }

        private boolean isTenantEmptyOfUser()
        {
            return !accountsService.hasUsers();
        }

        private boolean isCreateUserResource()
        {
            try {
                Class userResourceClass = Class.forName("org.mayocat.shop.api.v1.resources.UserResource");
                return userResourceClass.isAssignableFrom(this.method.getDeclaringResource().getResourceClass())
                        && this.method.getHttpMethod().equals(HttpMethod.POST)
                        && this.method.getResource().getPath().getValue().equals("/api/1.0/user/");
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Could not find user resource class", e);
            }
        }
    }



    /**
     * Request dispatcher provider that inspects Mayocat annotation on resources and declares the proper dispatchers
     * when necessary.
     */
    private class AnnotationResourceMethodDispatchProvider implements ResourceMethodDispatchProvider
    {
        private final ResourceMethodDispatchProvider provider;

        // FIXME : useless ?
        private final Execution execution;

        public AnnotationResourceMethodDispatchProvider(Execution execution, ResourceMethodDispatchProvider provider)
        {
            this.execution = execution;
            this.provider = provider;
        }

        @Override
        public RequestDispatcher create(AbstractResourceMethod method)
        {
            RequestDispatcher dispatcher = provider.create(method);
            if (dispatcher == null) {
                return null;
            }

            // Chain dispatchers required by resource declared associations
            // Highest priority last (it will be executed first)

            // Checks for methods or classes that requires authorization
            if (method.getMethod().isAnnotationPresent(Authorized.class)
                    || method.getClass().isAnnotationPresent(Authorized.class)) {

                Authorized annotation = method.getDeclaringResource().getAnnotation(Authorized.class);
                if (method.isAnnotationPresent(Authorized.class)) {
                    annotation = method.getAnnotation(Authorized.class);
                }

                dispatcher = new CheckAuthorizationMethodDispatcher(method, annotation, dispatcher);
            }

            // Checks for methods or classes that requires a valid tenant.
            if (method.getMethod().isAnnotationPresent(ExistingTenant.class)
                    || method.getDeclaringResource().isAnnotationPresent(ExistingTenant.class)) {
                dispatcher = new CheckValidTenantRequestDispatcher(dispatcher);
            }

            return dispatcher;
        }
    }

    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider)
    {
        return new AnnotationResourceMethodDispatchProvider(execution, provider);
    }
}
