/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.accounts.jersey;

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.mayocat.accounts.AccountsService;
import org.mayocat.accounts.api.v1.UserApi;
import org.mayocat.accounts.model.Role;
import org.mayocat.accounts.model.User;
import org.mayocat.authorization.Gatekeeper;
import org.mayocat.authorization.annotation.Authorized;
import org.mayocat.context.WebContext;
import org.mayocat.rest.Provider;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.rest.error.ErrorUtil;
import org.mayocat.rest.error.StandardError;
import org.xwiki.component.annotation.Component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResourceMethod;
import com.sun.jersey.spi.container.ResourceMethodDispatchAdapter;
import com.sun.jersey.spi.container.ResourceMethodDispatchProvider;
import com.sun.jersey.spi.dispatch.RequestDispatcher;

/**
 * Processes resources and check for the presence of Mayocat-specific annotation on the resource method and/or class and
 * instrument the resource accordingly if necessary.
 *
 * @version $Id$
 */
@Component("checkTenantAndUserMethodDispatch")
public class CheckTenantAndUserMethodDispatch implements ResourceMethodDispatchAdapter, Provider
{
    @Inject
    private WebContext context;

    @Inject
    private Gatekeeper gatekeeper;

    @Inject
    private AccountsService accountsService;

    /**
     * Request dispatcher that checks if a valid tenant has been set in the context, throws a NOT FOUND (404)
     * if none is found. <p /> Used when {@link org.mayocat.rest.annotation.ExistingTenant} annotation is present on a resource.
     */
    private class CheckValidTenantRequestDispatcher implements RequestDispatcher
    {
        private final RequestDispatcher underlying;

        public CheckValidTenantRequestDispatcher(RequestDispatcher underlying)
        {
            this.underlying = underlying;
        }

        @Override
        public void dispatch(Object resource, HttpContext httpContext)
        {
            if (context.getTenant() == null) {
                throw new WebApplicationException(
                        ErrorUtil.buildError(Response.Status.NOT_FOUND, StandardError.NOT_A_VALID_TENANT,
                                "No valid tenant at this address"));
            }
            underlying.dispatch(resource, httpContext);
        }
    }

    /**
     * Request dispatcher that checks the authorization level of the user set in the context relatively to the declared
     * clearance required by the {@link org.mayocat.authorization.annotation.Authorized} annotation on a resource.
     */
    private class CheckAuthorizationMethodDispatcher implements RequestDispatcher
    {
        private final AbstractResourceMethod method;

        private final RequestDispatcher underlying;

        private final Authorized annotation;

        public CheckAuthorizationMethodDispatcher(AbstractResourceMethod method, Authorized authorizedAnnotation,
                RequestDispatcher underlying)
        {
            this.underlying = underlying;
            this.method = method;
            this.annotation = authorizedAnnotation;
        }

        @Override
        public void dispatch(Object resource, HttpContext httpContext)
        {
            User user = context.getUser();

            if (user != null) {
                if (!this.checkAuthorization(user)) {
                    throw new WebApplicationException(Response.status(Response.Status.FORBIDDEN)
                            .entity("Insufficient privileges").type(MediaType.TEXT_PLAIN_TYPE).build());
                }
            } else {
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

        private boolean checkAuthorization(User user)
        {
            if (annotation.requiresGlobalUser() && !user.isGlobal()) {
                return false;
            }

            if (this.annotation.roles().length == 0 && user != null) {
                // No specific role is required, just an authenticated user
                return true;
            }

            for (Role role : this.annotation.roles()) {
                if (gatekeeper.userHasRole(user, role)) {
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
            return UserApi.class.isAssignableFrom(this.method.getDeclaringResource().getResourceClass())
                    && this.method.getHttpMethod().equals(HttpMethod.POST)
                    && this.method.getResource().getPath().getValue().equals("/api/user");
        }
    }

    /**
     * Request dispatcher provider that inspects Mayocat annotation on resources and declares the proper dispatchers
     * when necessary.
     */
    private class AnnotationResourceMethodDispatchProvider implements ResourceMethodDispatchProvider
    {
        private final ResourceMethodDispatchProvider provider;

        public AnnotationResourceMethodDispatchProvider(ResourceMethodDispatchProvider provider)
        {
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
                    || method.getDeclaringResource().getResourceClass().isAnnotationPresent(Authorized.class))
            {

                Authorized annotation;
                if (method.isAnnotationPresent(Authorized.class)) {
                    annotation = method.getAnnotation(Authorized.class);
                } else {
                    annotation = method.getDeclaringResource().getResourceClass().getAnnotation(Authorized.class);
                }

                dispatcher = new CheckAuthorizationMethodDispatcher(method, annotation, dispatcher);
            }

            // Checks for methods or classes that requires a valid tenant.
            if (method.getMethod().isAnnotationPresent(ExistingTenant.class)
                    || method.getDeclaringResource().isAnnotationPresent(ExistingTenant.class))
            {
                dispatcher = new CheckValidTenantRequestDispatcher(dispatcher);
            }

            return dispatcher;
        }
    }

    @Override
    public ResourceMethodDispatchProvider adapt(ResourceMethodDispatchProvider provider)
    {
        return new AnnotationResourceMethodDispatchProvider(provider);
    }
}
