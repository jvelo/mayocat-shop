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
import org.mayocat.accounts.AccountsService
import org.mayocat.accounts.AccountsSettings
import org.mayocat.accounts.IncompatibleConnectedUserException
import org.mayocat.accounts.NoSuchValidationKeyException
import org.mayocat.accounts.PasswordDoesNotMeetRequirementsException
import org.mayocat.accounts.UserAlreadyValidatedException
import org.mayocat.accounts.WrongPasswordException
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
import org.mayocat.rest.error.Error
import org.mayocat.rest.error.StandardError
import org.mayocat.shop.billing.model.Order
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
@Component("/marketplace/account/")
@Path("/marketplace/account/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@CompileStatic
class CustomerAccountWebView extends TenantCustomerAccountWebView
{
}
