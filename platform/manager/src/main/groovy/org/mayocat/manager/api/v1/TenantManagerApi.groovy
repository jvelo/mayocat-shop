/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.manager.api.v1

import com.google.common.base.Optional
import com.google.common.base.Strings
import groovy.transform.CompileStatic
import org.joda.time.DateTimeZone
import org.mayocat.Slugifier
import org.mayocat.accounts.AccountsService
import org.mayocat.accounts.PasswordDoesNotMeetRequirementsException
import org.mayocat.accounts.api.v1.object.TenantApiObject
import org.mayocat.accounts.api.v1.object.UserAndTenantApiObject
import org.mayocat.accounts.model.Role
import org.mayocat.accounts.model.Tenant
import org.mayocat.accounts.model.User
import org.mayocat.accounts.store.TenantStore
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.authorization.Gatekeeper
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.MultitenancySettings
import org.mayocat.configuration.PlatformSettings
import org.mayocat.configuration.general.GeneralSettings
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityData
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.image.model.Image
import org.mayocat.image.model.ImageGallery
import org.mayocat.manager.api.v1.object.TenantListApiObject
import org.mayocat.rest.Resource
import org.mayocat.rest.api.object.Pagination
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.InvalidEntityException
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.validation.Valid
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/management/api/tenants")
@Path("/management/api/tenants")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class TenantManagerApi implements Resource
{
    @Inject
    EntityDataLoader dataLoader

    @Inject
    WebContext context

    @Inject
    AccountsService accountsService

    @Inject
    Provider<TenantStore> tenantStore

    @Inject
    Gatekeeper gatekeeper

    @Inject
    GeneralSettings generalSettings

    @Inject
    MultitenancySettings multitenancySettings

    @Inject
    Slugifier slugifier

    @Inject
    PlatformSettings platformSettings

    @GET
    @Path("{slug}")
    @Authorized(roles = Role.ADMIN, requiresGlobalUser = true)
    TenantApiObject getTenant(@PathParam("slug") String slug)
    {
        Tenant tenant = accountsService.findTenant(slug)
        if (tenant == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND)
        }

        TenantApiObject tenantApiObject = new TenantApiObject([
                _href: "/management/api/tenant/"
        ])

        tenantApiObject.withTenant(tenant, globalTimeZone)
        tenantApiObject
    }

    @GET
    @Authorized(roles = Role.ADMIN, requiresGlobalUser = true)
    def getAllTenants(@QueryParam("number") @DefaultValue("50") Integer limit,
            @DefaultValue("0") @QueryParam("offset") Integer offset)
    {
        List<Tenant> tenants = accountsService.findAllTenants(limit, offset)
        Integer total = this.accountsService.countAllTenants()

        if (tenants == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND)
        }

        List<EntityData<Tenant>> tenantsData = dataLoader.load(tenants, StandardOptions.LOCALIZE,
                AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)

        def tenantList = [] as List<TenantApiObject>

        tenantsData.each({ EntityData<Tenant> tenantData ->
            Tenant tenant = tenantData.entity
            TenantApiObject tenantApiObject = new TenantApiObject([
                    _href: "/management/api/tenants/${tenant.slug}"
            ])

            tenantApiObject.withTenant(tenant, globalTimeZone)

            def gallery = tenantData.getData(ImageGallery.class)
            List<Image> images = gallery.isPresent() ? gallery.get().images : [] as List<Image>

            tenantApiObject.withEmbeddedImages(images, tenant.featuredImageId, "/tenant/${tenant.slug}")

            tenantList << tenantApiObject
        })

        new TenantListApiObject([
                _pagination: new Pagination([
                        numberOfItems: limit,
                        returnedItems: tenantList.size(),
                        offset       : offset,
                        totalItems   : total,
                        urlTemplate  : '/management/api/tenants?number=${numberOfItems}&offset=${offset}'
                ]),
                tenants    : tenantList
        ])
    }

    @POST
    @Path("{slug}")
    @Authorized(roles = Role.ADMIN, requiresGlobalUser = true)
    Response updateTenant(@PathParam("slug") String slug, TenantApiObject tenantApiObject)
    {
        Tenant tenant = tenantApiObject.toTenant(platformSettings, Optional.absent())

        // prevent from updating the slug
        tenant.slug = slug

        try {
            this.accountsService.updateTenant(tenant)
            return Response.ok().build()
        } catch (InvalidEntityException e) {
            return Response.status(422).entity("Invalid entity").build()
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @DELETE
    @Path("{slug}")
    @Authorized(roles = Role.ADMIN, requiresGlobalUser = true)
    Response deleteTenant(@PathParam("slug") String slug)
    {
        Tenant tenant = this.accountsService.findTenant(slug)

        if (!tenant) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }

        try {
            this.tenantStore.get().delete(tenant)
            return Response.ok().build()
        } catch (InvalidEntityException e) {
            return Response.status(422).entity("Invalid entity").build()
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }
    }

    @POST
    Response createTenant(@Valid UserAndTenantApiObject userAndTenant)
    {
        if (!multitenancySettings.isActivated()) {
            return Response.status(Response.Status.FORBIDDEN).entity("Tenant creation is not allowed on this server\n")
                    .build()
        }

        if (!isTenantCreationAllowed()) {
            return Response.status(Response.Status.FORBIDDEN).build()
        }

        if (!userAndTenant.tenant || Strings.isNullOrEmpty(userAndTenant.tenant.name)) {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }

        try {
            Tenant tenant = userAndTenant.tenant.toTenant(platformSettings, Optional.absent())
            tenant.setCreationDate(new Date())

            if (Strings.isNullOrEmpty(tenant.slug)) {
                tenant.slug = slugifier.slugify(tenant.name)
            }

            accountsService.createTenant(tenant)

            if (userAndTenant.user != null) {
                // Set tenant in context so that user is created for that tenant
                context.setTenant(tenant)

                try {
                    accountsService.createInitialUser(userAndTenant.user.toUser())
                } catch (EntityAlreadyExistsException e) {
                    return Response.status(Response.Status.CONFLICT).entity("Initial user already exists\n").build()
                } catch (PasswordDoesNotMeetRequirementsException e) {
                    return Response.status(Response.Status.BAD_REQUEST)
                            .entity("Password does not meet requirements\n").build()
                }
            }

            return Response.created(new URI(tenant.slug)).build();

        } catch (EntityAlreadyExistsException e) {
            return Response.status(Response.Status.CONFLICT).entity("A tenant with this slug already exists").build()
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST).build()
        }
    }

    boolean isTenantCreationAllowed()
    {
        if (multitenancySettings.getRequiredRoleForTenantCreation() != Role.NONE) {

            User contextUser = context.user
            if (contextUser == null || !contextUser.isGlobal()) {
                return false
            }

            return gatekeeper.userHasRole(context.getUser(),
                    multitenancySettings.getRequiredRoleForTenantCreation())
        } else {
            return true
        }
    }

    DateTimeZone getGlobalTimeZone()
    {
        return DateTimeZone.forTimeZone(generalSettings.getTime().getTimeZone().getDefaultValue())
    }
}
