/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.customer.web

import com.google.common.base.Strings
import com.google.common.collect.Lists
import com.google.common.collect.Maps
import groovy.transform.CompileStatic
import org.mayocat.accounts.*
import org.mayocat.accounts.model.Tenant
import org.mayocat.accounts.model.User
import org.mayocat.accounts.session.JerseyCookieSessionManager
import org.mayocat.accounts.web.object.UserValidationWebObject
import org.mayocat.accounts.web.object.UserWebObject
import org.mayocat.attachment.AttachmentLoadingOptions
import org.mayocat.authorization.annotation.Authorized
import org.mayocat.configuration.ConfigurationService
import org.mayocat.context.WebContext
import org.mayocat.entity.EntityDataLoader
import org.mayocat.entity.StandardOptions
import org.mayocat.rest.Resource
import org.mayocat.rest.annotation.ExistingTenant
import org.mayocat.rest.error.Error
import org.mayocat.rest.error.StandardError
import org.mayocat.shop.billing.model.Order
import org.mayocat.shop.billing.model.OrderItem
import org.mayocat.shop.billing.store.OrderStore
import org.mayocat.shop.catalog.store.ProductStore
import org.mayocat.shop.catalog.web.object.PriceWebObject
import org.mayocat.shop.customer.model.Address
import org.mayocat.shop.customer.model.Customer
import org.mayocat.shop.customer.store.AddressStore
import org.mayocat.shop.customer.store.CustomerStore
import org.mayocat.shop.customer.web.object.*
import org.mayocat.shop.front.views.WebView
import org.mayocat.shop.marketplace.web.delegate.WithProductWebObjectBuilder
import org.mayocat.shop.marketplace.web.object.MarketplaceProductWebObject
import org.mayocat.store.EntityAlreadyExistsException
import org.mayocat.store.EntityDoesNotExistException
import org.xwiki.component.annotation.Component

import javax.inject.Inject
import javax.inject.Provider
import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

/**
 * @version $Id$
 */
@Component("/account/")
@Path("/account/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
@ExistingTenant
class TenantCustomerAccountWebView implements Resource, WithProductWebObjectBuilder
{
    @Inject
    AccountsService accountsService

    @Inject
    Provider<AddressStore> addressStore

    @Inject
    CustomerStore customerStore

    @Inject
    ConfigurationService configurationService

    @Inject
    JerseyCookieSessionManager sessionManager

    @Inject
    Provider<OrderStore> orderStore

    @Inject
    Provider<ProductStore> productStore

    @Inject
    EntityDataLoader dataLoader

    @Inject
    WebContext context

    @POST
    def createCustomerAccount(CustomerAccountCreationWebObject request)
    {
        Map<String, Object> data = Maps.newHashMap()
        Map<String, Object> accountCreationData = Maps.newHashMap()

        // Put back request data in the context (in case of failure, to fill back the form)
        accountCreationData.put("request", request)

        AccountsSettings accountsSettings = configurationService.getSettings(AccountsSettings.class)

        UserAccountWebObject accountWebObject = request.account
        CustomerWebObject customerWebObject = request.customer

        if (!accountWebObject || Strings.isNullOrEmpty(accountWebObject.email) ||
                Strings.isNullOrEmpty(accountWebObject.password))
        {
            accountCreationData.put("error",
                    new Error(Response.Status.BAD_REQUEST, StandardError.INSUFFICIENT_DATA, "Invalid user account"))
        } else if (!customerWebObject || Strings.isNullOrEmpty(customerWebObject.firstName) ||
                Strings.isNullOrEmpty(customerWebObject.lastName))
        {
            accountCreationData.put("error", new Error(Response.Status.BAD_REQUEST, StandardError.INSUFFICIENT_DATA,
                    "Invalid customer information"))
        }

        if (!accountCreationData.containsKey("error")) {

            User user = accountWebObject.toUser()
            user.global = true
            String password = user.password // Reference kept for auto-login, see below

            if (Strings.isNullOrEmpty(user.slug)) {
                // The notion of "username" does not always make sense for an e-commerce or marketplace platform.
                // In such cases, we generate a slug for the user based on the email used.
                // TODO: configuration option to have username (slug) optional or not.
                user.slug = user.email
            }

            Customer customer = customerWebObject.toCustomer()
            if (Strings.isNullOrEmpty(customer.email)) {
                customer.email = user.email
            }
            customer.slug = customer.email

            try {
                this.accountsService.createUser(user, [
                        customer: [
                                firstName: customer.firstName,
                                lastName : customer.lastName
                        ]
                ] as Map<String, Object>)
                user = accountsService.findUserByEmailOrUserName(user.email)

                customer.userId = user.id
                Customer createdCustomer = customerStore.create(customer)

                if (customerWebObject.deliveryAddress) {
                    Address deliveryAddress = customerWebObject.deliveryAddress.toAddress()
                    deliveryAddress.customerId = createdCustomer.id
                    deliveryAddress.type = "delivery"
                    this.addressStore.get().create(deliveryAddress)
                }

                if (customerWebObject.billingAddress) {
                    Address billingAddress = customerWebObject.billingAddress.toAddress()
                    billingAddress.customerId = createdCustomer.id
                    billingAddress.type = "billing"
                    this.addressStore.get().create(billingAddress)
                }

                accountCreationData.put("successful", true)
                data.put("accountCreation", accountCreationData)

                if (!accountCreationData.containsKey("error")) {
                    if (accountsSettings.isAutoLoginAfterSignup().value) {

                        // Put the user and customer in context
                        data.put("user", new UserWebObject().withUser(user))
                        data.put("customer", customerWebObject)

                        // Response with login cookies
                        return Response.ok().entity(new WebView().data(data)).
                                cookie(sessionManager.getCookies(user.slug, password, false)).build()
                    } else {
                        return new WebView().data(data)
                    }
                }
            } catch (PasswordDoesNotMeetRequirementsException e) {
                accountCreationData.put("error", new Error(Response.Status.BAD_REQUEST,
                        StandardError.PASSWORD_DOES_NOT_MEET_REQUIREMENTS,
                        "Provided new password does not meet requirements"))
            } catch (EntityAlreadyExistsException e) {
                accountCreationData.
                        put("error", new Error(Response.Status.CONFLICT, StandardError.EMAIL_ALREADY_REGISTERED,
                                "User with email exists"))
                // TODO handle username already exists
            }
        }
        accountCreationData.put("successful", false)
        data.put("accountCreation", accountCreationData)

        return Response.status(((Error) accountCreationData.get("error")).getStatus()).
                entity(new WebView().data(data)).build()
    }

    @POST
    @Path("details")
    @Authorized
    def updateCustomerDetails(CustomerWebObject customerWebObject)
    {
        Map<String, Object> data = Maps.newHashMap()
        Map<String, Object> customerDetailsUpdate = Maps.newHashMap()

        data.put("customerDetailsUpdate", customerDetailsUpdate)

        if (!customerWebObject) {
            customerDetailsUpdate.put("error",
                    new Error(Response.Status.BAD_REQUEST, StandardError.INSUFFICIENT_DATA, "Invalid user account"))
        } else {
            Customer storedCustomer = this.customerStore.findByUserId(context.user.id)
            Customer customer = customerWebObject.toCustomer()
            customer.id = storedCustomer.id
            customer.slug = storedCustomer.slug
            if (storedCustomer.userId.isPresent()) {
                customer.setUserId storedCustomer.userId.get()
            }

            try {
                this.customerStore.update(customer)

                Address storedDeliveryAddress = this.addressStore.get().
                        findByCustomerIdAndType(storedCustomer.id, 'delivery')
                Address storedBillingAddress = this.addressStore.get().
                        findByCustomerIdAndType(storedCustomer.id, 'billing')

                if (customerWebObject.deliveryAddress) {
                    // Update or create delivery address

                    Address delivery = customerWebObject.deliveryAddress.toAddress()
                    delivery.customerId = storedCustomer.id
                    delivery.type = 'delivery'

                    if (!storedDeliveryAddress) {
                        this.addressStore.get().create(delivery)
                    } else {
                        delivery.id = storedDeliveryAddress.id
                        this.addressStore.get().update(delivery)
                    }
                } else if (storedDeliveryAddress) {
                    this.addressStore.get().delete(storedDeliveryAddress)
                }

                if (customerWebObject.billingAddress) {
                    // Update or create billing address

                    Address billing = customerWebObject.billingAddress.toAddress()
                    billing.customerId = storedCustomer.id
                    billing.type = 'billing'

                    if (!storedBillingAddress) {
                        this.addressStore.get().create(billing)
                    } else {
                        billing.id = storedBillingAddress.id
                        this.addressStore.get().update(billing)
                    }
                } else if (storedBillingAddress) {
                    this.addressStore.get().delete(storedBillingAddress)
                }
            } catch (EntityDoesNotExistException e) {
                customerDetailsUpdate.
                        put("error", new Error(Response.Status.NOT_FOUND, CustomerAccountError.CUSTOMER_NOT_FOUND,
                                "Could not find this customer"))
            }
        }

        if (customerDetailsUpdate.containsKey("error")) {
            return Response.status(((Error) customerDetailsUpdate.get("error")).getStatus()).
                    entity(new WebView().data(data)).build()
        } else {
            return new WebView().data(data)
        }
    }

    @POST
    @Path("validate")
    def validateCustomerAccount(UserValidationWebObject validationWebObject)
    {
        Map<String, Object> data = Maps.newHashMap()
        Map<String, Object> validationData = Maps.newHashMap()

        try {
            accountsService.validateAccount(validationWebObject.validationId)
        }
        catch (IncompatibleConnectedUserException e) {
            validationData.put("error", new Error(Response.Status.BAD_REQUEST,
                    CustomerAccountError.CONNECTED_USER_DOES_NOT_MATCH,
                    "Connected user does not match validation key"))
        }
        catch (NoSuchValidationKeyException e) {
            validationData.put("error", new Error(Response.Status.BAD_REQUEST,
                    CustomerAccountError.VALIDATION_KEY_DOES_NOT_EXIST, "Validation key does not exist"))
        }
        catch (UserAlreadyValidatedException e) {
            validationData.put("error", new Error(Response.Status.BAD_REQUEST,
                    CustomerAccountError.ACCOUNT_ALREADY_VALIDATED, "User is already validated"))
        }

        validationData.put("successful", !validationData.containsKey("error"))
        data.put("accountValidation", validationData)

        return new WebView().data(data)
    }

    @POST
    @Path("password")
    @Authorized
    def changeCustomerAccountPassword(PasswordChangeWebObject passwordChange)
    {
        Map<String, Object> data = Maps.newHashMap()
        Map<String, Object> passwordChangeData = Maps.newHashMap()

        if (!context.user) {
            passwordChangeData.put("error", new Error(Response.Status.UNAUTHORIZED, StandardError.REQUIRES_VALID_USER,
                    "Can't change the password for nobody"))
        } else {
            try {
                accountsService.changePassword(context.user, passwordChange.currentPassword, passwordChange.newPassword)
            } catch (WrongPasswordException e) {
                passwordChangeData.
                        put("error", new Error(Response.Status.UNAUTHORIZED, StandardError.INVALID_CREDENTIALS,
                                "Credentials are not correct"))
            } catch (PasswordDoesNotMeetRequirementsException e) {
                passwordChangeData.put("error", new Error(Response.Status.BAD_REQUEST,
                        StandardError.PASSWORD_DOES_NOT_MEET_REQUIREMENTS,
                        "Provided new password does not meet requirements"))
            }
        }

        passwordChangeData.put("successful", !passwordChangeData.containsKey("error"))
        data.put("passwordChange", passwordChangeData)

        if (!passwordChangeData.containsKey("error")) {
            // Connect with new password
            return Response.ok().entity(new WebView().data(data))
                    .cookie(sessionManager.getCookies(context.user.slug, passwordChange.newPassword, false)).build()
        }

        return new WebView().data(data)
    }

    @GET
    @Path("orders")
    @Authorized
    def getMyOrders()
    {
        Customer me = this.customerStore.findByUserId(context.user.id)
        List<Order> orders = orderStore.get().findAllPaidForCustomer(me.id)
        List<OrderSummaryWebObject> result = Lists.newArrayList()

        orders.each({ Order order ->
            OrderSummaryWebObject orderWebObject = new OrderSummaryWebObject()
            orderWebObject.withOrder(order, context.locale)

            result << orderWebObject
        })

        return new WebView().data([orders: result] as Map<String, Object>)
    }

    @GET
    @Path("orders/{slug}")
    @Authorized
    def getMyOrder(@PathParam("slug") String slug)
    {
        Customer me = this.customerStore.findByUserId(context.user.id)
        Order order = this.orderStore.get().findBySlug(slug)

        if (!order) {
            return new WebView().data(
                    [error: new Error(Response.Status.NOT_FOUND, StandardError.ORDER_NOT_FOUND,
                            "Not authorized")] as Map<String, Object>)
        }

        if (order.customerId != me.id) {
            return new WebView().data(
                    [error: new Error(Response.Status.UNAUTHORIZED, StandardError.INSUFFICIENT_PRIVILEGES,
                            "Not authorized")] as Map<String, Object>)
        }

        OrderWebObject orderWebObject = new OrderWebObject().withOrder(order, context.locale)

        List<OrderItem> orderItems = order.getOrderItems();

        orderItems.each({OrderItem item ->

            OrderItemWebObject orderItemWebObject = new OrderItemWebObject()
            orderItemWebObject.withOrderItem(item, order.currency, context.locale)
            
            def productId = item.purchasableId
            if (productId) {
                def product = productStore.get().findById(productId)
                if (product) {
                    def productData = dataLoader.
                            load(product, StandardOptions.LOCALIZE, AttachmentLoadingOptions.FEATURED_IMAGE_ONLY)
                    def tenant = productData.getData(Tenant.class)
                    MarketplaceProductWebObject productWebObject = buildProductWebObject(tenant.orNull(), productData)

                    orderItemWebObject.product = productWebObject
                }
            }

            orderWebObject.items << orderItemWebObject
        })

        return new WebView().data([order: orderWebObject] as Map<String, Object>)
    }
}
