/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.context.WebContext;
import org.mayocat.entity.EntityDataLoader;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.cart.Cart;
import org.mayocat.shop.cart.CartManager;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.checkout.CheckoutException;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CheckoutResponse;
import org.mayocat.shop.checkout.CheckoutSettings;
import org.mayocat.shop.checkout.RegularCheckoutException;
import org.mayocat.shop.checkout.front.CheckoutResource;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;
import org.mayocat.shop.customer.store.AddressStore;
import org.mayocat.shop.customer.store.CustomerStore;
import org.mayocat.shop.payment.BasePaymentData;
import org.mayocat.shop.payment.GatewayException;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.GatewayResponse;
import org.mayocat.shop.payment.PaymentData;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.event.PaymentOperationEvent;
import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.PaymentOperationStore;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.taxes.configuration.TaxesSettings;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.url.URLHelper;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.ObservationManager;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component
public class DefaultCheckoutRegister implements CheckoutRegister
{
    @Inject
    private Logger logger;

    @Inject
    private EntityDataLoader dataLoader;

    @Inject
    private PlatformSettings platformSettings;

    @Inject
    private CheckoutSettings checkoutSettings;

    @Inject
    private TaxesSettings taxesSettings;

    @Inject
    private Provider<OrderStore> orderStore;

    @Inject
    private Provider<CustomerStore> customerStore;

    @Inject
    private Provider<AddressStore> addressStore;

    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Map<String, GatewayFactory> gatewayFactories;

    @Inject
    private Provider<PaymentOperationStore> paymentOperationStore;

    @Inject
    private ShippingService shippingService;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private CartManager cartManager;

    @Inject
    private WebContext webContext;

    @Inject
    private SiteSettings siteSettings;

    @Inject
    private URLHelper urlHelper;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Override
    public CheckoutResponse checkout(Customer customer, Address deliveryAddress,
            Address billingAddress, Map<String, Object> extraOrderData) throws CheckoutException
    {
        Preconditions.checkNotNull(customer);
        Order order;
        Customer actualCustomer;

        final Cart cart = cartManager.getCart();

        try {
            UUID customerId;
            UUID deliveryAddressId = null;
            UUID billingAddressId = null;
            Map<String, Object> data = Maps.newHashMap(); // Order JSON data

            if (customer.getId() == null) {
                customer.setSlug(customer.getEmail());
                if (this.customerStore.get().findBySlug(customer.getEmail()) == null) {
                    actualCustomer = this.customerStore.get().create(customer);
                } else {
                    Customer existingCustomer = this.customerStore.get().findBySlug(customer.getEmail());
                    boolean update = updateCustomerIfNecessary(existingCustomer, customer);
                    if (update) {
                        this.customerStore.get().update(existingCustomer);
                    }
                    actualCustomer = existingCustomer;
                }
                customerId = actualCustomer.getId();

                if (deliveryAddress != null) {
                    deliveryAddress = this.addressStore.get().create(deliveryAddress);
                    deliveryAddressId = deliveryAddress.getId();
                }
                if (billingAddress != null) {
                    billingAddress = this.addressStore.get().create(billingAddress);
                    billingAddressId = billingAddress.getId();
                }
            } else {
                actualCustomer = customer;
                customerId = customer.getId();
                deliveryAddressId = deliveryAddress.getId();
                if (billingAddress != null) {
                    billingAddressId = billingAddress.getId();
                }
            }

            order = new Order();
            order.setBillingAddressId(billingAddressId);
            order.setDeliveryAddressId(deliveryAddressId);
            order.setCustomerId(customerId);
            if (extraOrderData.containsKey("additionalInformation")) {
                order.setAdditionalInformation((String) extraOrderData.get("additionalInformation"));
            }
            if (extraOrderData.containsKey("extraData")) {
                data.put(Order.ORDER_DATA_EXTRA, (Map<String, Object>) extraOrderData.get("extraData"));
            }

            // Items
            List<OrderItem> items = FluentIterable.from(cart.items()).transform(new CartItemToOrderItemTransformer(
                    dataLoader,
                    platformSettings,
                    productStore.get(),
                    taxesSettings,
                    webContext
            )).toList();
            order.setItemsTotal(cart.itemsTotal().incl());
            order.setItemsTotalExcl(cart.itemsTotal().excl());
            order.setOrderItems(items);
            order.setNumberOfItems(Long.valueOf(items.size()));

            // Shipping
            if (cart.selectedShippingOption().isPresent()) {
                final Carrier carrier = shippingService.getCarrier(
                        cart.selectedShippingOption().get().getCarrierId());
                order.setShipping(cart.selectedShippingOption().get().getPrice().incl());
                order.setShippingExcl(cart.selectedShippingOption().get().getPrice().excl());
                data.put(Order.ORDER_DATA_SHIPPING, new HashMap<String, Object>()
                {
                    {
                        put("carrierId", carrier.getId());
                        put("title", carrier.getTitle());
                        put("strategy", carrier.getStrategy());
                        put("vatRate", carrier.getVatRate().doubleValue());
                    }
                });
            }

            // Dates, currency, status
            order.setCreationDate(new Date());
            order.setUpdateDate(order.getCreationDate());
            order.setCurrency(cart.currency());
            order.setStatus(Order.Status.NONE);

            // Grand total
            order.setGrandTotal(cart.total().incl());
            order.setGrandTotalExcl(cart.total().excl());

            // JSON data
            order.setOrderData(data);

            order = orderStore.get().create(order);
        } catch (EntityAlreadyExistsException | EntityDoesNotExistException | InvalidEntityException e) {
            throw new CheckoutException(e);
        }

        String defaultGatewayFactory = checkoutSettings.getDefaultPaymentGateway();

        // Right now only the default gateway factory is supported.
        // In the future individual tenants will be able to setup their own payment gateway.

        if (!gatewayFactories.containsKey(defaultGatewayFactory)) {
            throw new CheckoutException("No gateway factory is available to handle the checkout.");
        }

        GatewayFactory factory = gatewayFactories.get(defaultGatewayFactory);
        PaymentGateway gateway = factory.createGateway();
        if (gateway == null) {
            throw new CheckoutException("Gateway could not be created.");
        }

        Map<PaymentData, Object> data = Maps.newHashMap();

        data.put(BasePaymentData.BASE_WEB_URL, urlHelper.getContextWebBaseURL());
        data.put(BasePaymentData.BASE_PLATFORM_URL, urlHelper.getContextPlatformBaseURL());
        data.put(BasePaymentData.CANCEL_URL,
                urlHelper.getContextWebURL(CheckoutResource.PATH + "/" + CheckoutResource.PAYMENT_CANCEL_PATH + "/"
                        + order.getId()).toString());
        data.put(BasePaymentData.RETURN_URL, urlHelper.getContextWebURL(CheckoutResource.PATH + "/"
                + CheckoutResource.PAYMENT_RETURN_PATH + "/" + order.getId()).toString());
        data.put(BasePaymentData.CURRENCY, cart.currency());
        data.put(BasePaymentData.ORDER_ID, order.getId());
        data.put(BasePaymentData.CUSTOMER, actualCustomer);
        if (billingAddress != null) {
            data.put(BasePaymentData.BILLING_ADDRESS, billingAddress);
        }
        data.put(BasePaymentData.DELIVERY_ADDRESS, deliveryAddress);
        data.put(BasePaymentData.ORDER, order);

        try {
            CheckoutResponse response = new CheckoutResponse();
            response.setOrder(order);
            GatewayResponse gatewayResponse = gateway.purchase(cart.total().incl(), data);
            response.setData(gatewayResponse.getData());

            if (gatewayResponse.isSuccessful()) {

                if (gatewayResponse.isRedirection()) {
                    response.setRedirectURL(Optional.fromNullable(gatewayResponse.getRedirectURL()));
                }

                cartManager.discardCart();

                if (gatewayResponse.getOperation().getResult().equals(PaymentOperation.Result.CAPTURED)) {
                    order.setStatus(Order.Status.PAID);
                } else {
                    order.setStatus(Order.Status.PAYMENT_PENDING);
                }

                try {
                    orderStore.get().update(order);
                    PaymentOperation operation = gatewayResponse.getOperation();
                    operation.setOrderId(order.getId());
                    paymentOperationStore.get().create(operation);

                    observationManager.notify(new PaymentOperationEvent(), gatewayResponse.getOperation());
                } catch (EntityDoesNotExistException | InvalidEntityException | EntityAlreadyExistsException e) {
                    this.logger.error("Order error while checking out cart", e);
                    throw new CheckoutException(e);
                }

                return response;
            } else {
                throw new CheckoutException("Payment was not successful");
            }
        } catch (GatewayException e) {
            this.logger.error("Payment error while checking out cart", e);
            throw new CheckoutException(e);
        }
    }

    @Override
    public void dropOrder(UUID orderId) throws CheckoutException
    {
        Order order = orderStore.get().findById(orderId);
        if (order == null) {
            throw new RegularCheckoutException("Order with id [" + orderId.toString() + "] does not exist.");
        }
        try {
            order.setStatus(Order.Status.CANCELLED);
            orderStore.get().update(order);
        } catch (EntityDoesNotExistException | InvalidEntityException e) {
            throw new CheckoutException(e);
        }
    }

    @Override
    public boolean requiresForm()
    {
        return true;
    }

    private boolean updateCustomerIfNecessary(Customer existingCustomer, Customer customer)
    {
        boolean update = false;
        if (existingCustomer.getFirstName() == null ||
                !existingCustomer.getFirstName().equals(customer.getFirstName()))
        {
            update = true;
            existingCustomer.setFirstName(customer.getFirstName());
        }
        if (existingCustomer.getLastName() == null ||
                !existingCustomer.getLastName().equals(customer.getLastName()))
        {
            update = true;
            existingCustomer.setLastName(customer.getLastName());
        }
        if (existingCustomer.getPhoneNumber() == null ||
                !existingCustomer.getPhoneNumber().equals(customer.getPhoneNumber()))
        {
            update = true;
            existingCustomer.setPhoneNumber(customer.getPhoneNumber());
        }
        return update;
    }
}
