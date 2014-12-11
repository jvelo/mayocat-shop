/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.api.v1

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.configuration.PlatformSettings
import org.mayocat.context.WebContext
import org.mayocat.rest.Resource
import org.mayocat.rest.api.object.AddonGroupApiObject
import org.mayocat.rest.api.object.LinkApiObject
import org.mayocat.rest.api.object.Pagination
import org.mayocat.shop.customer.api.v1.object.CustomerApiObject
import org.mayocat.shop.customer.api.v1.object.CustomerListApiObject
import org.mayocat.shop.customer.model.Address
import org.mayocat.shop.customer.model.Customer
import org.mayocat.shop.customer.store.AddressStore
import org.mayocat.shop.customer.store.CustomerStore
import org.mayocat.store.EntityDoesNotExistException
import org.mayocat.store.InvalidEntityException
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/api/customers")
@Path("/api/customers")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class CustomerApi implements Resource
{
    @Inject
    Provider<CustomerStore> customerStore

    @Inject
    Provider<AddressStore> addressStore

    @Inject
    PlatformSettings platformSettings

    @Inject
    WebContext webContext

    @JsonInclude(JsonInclude.Include.NON_NULL)
    Map<String, AddonGroupApiObject> addons

    @GET
    def getCustomers(@QueryParam("number") @DefaultValue("50") Integer number,
            @QueryParam("offset") @DefaultValue("0") Integer offset,
            @QueryParam("matches") @DefaultValue("") String matches)
    {
        List<Customer> customers = customerStore.get().findAll(number, offset)
        Integer totalCustomers = customerStore.get().countAll();

        def customerList = customers.collect({ Customer customer ->
            def customerApiObject = new CustomerApiObject([
                    _href: "${webContext.request.tenantPrefix}/api/customers/${customer.slug}"
            ])
            customerApiObject.withCustomer(customer)

            if (customer.addons.isLoaded()) {
                customerApiObject.withAddons(customer.addons.get())
            }

            customerApiObject
        })

        def customerListResult = new CustomerListApiObject([
                _pagination: new Pagination([
                        numberOfItems: number,
                        returnedItems: customerList.size(),
                        offset       : offset,
                        totalItems   : totalCustomers,
                        urlTemplate  : '${tenantPrefix}/api/customers?number=${numberOfItems}&offset=${offset}&matches=${matches}&',
                        urlArguments : [
                                matches     : matches,
                                tenantPrefix: webContext.request.tenantPrefix

                        ]
                ]),
                customers  : customerList
        ])

        customerListResult
    }

    @GET
    @Path("{customer}")
    def getCustomer(@PathParam("customer") String slug)
    {
        Customer customer = customerStore.get().findBySlug(slug)
        if (!customer) {
            return Response.status(Response.Status.NOT_FOUND).build()
        }
        CustomerApiObject customerApiObject = new CustomerApiObject([
                _href : "${webContext.request.tenantPrefix}/api/customers/${customer.slug}",
                _links: [
                        self  : new LinkApiObject(
                                [href: "${webContext.request.tenantPrefix}/api/customers/${customer.slug}"]),
                        orders: new LinkApiObject(
                                [href: "${webContext.request.tenantPrefix}/api/orders/?customer=${customer.slug}"])
                ]
        ]).withCustomer(customer).withAddons(customer.addons.get())

        Address billingAddress = addressStore.get().findByCustomerIdAndType(customer.id, "billing")
        if (billingAddress) {
            customerApiObject.withEmbeddedBillingAddress(billingAddress)
        }

        Address deliveryAddress = addressStore.get().findByCustomerIdAndType(customer.id, "delivery")
        if (deliveryAddress) {
            customerApiObject.withEmbeddedDeliveryAddress(deliveryAddress)
        }

        customerApiObject
    }

    @POST
    @Path("{customer}")
    def updateCustomer(@PathParam("customer") String slug, CustomerApiObject customerApiObject)
    {
        Customer retrieved = this.customerStore.get().findBySlug(slug)
        if (retrieved == null) {
            return Response.status(404).build()
        }

        def customer = customerApiObject.toCustomer(platformSettings, Optional.absent());

        // Id and slug can't be updated
        customer.slug = slug;
        customer.id = retrieved.id

        try {
            customerStore.get().update(customer)

            return Response.ok().build()
        } catch (InvalidEntityException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Invalid article\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        } catch (EntityDoesNotExistException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No Article with this slug could be found\n").type(MediaType.TEXT_PLAIN_TYPE).build()
        }
    }
}
