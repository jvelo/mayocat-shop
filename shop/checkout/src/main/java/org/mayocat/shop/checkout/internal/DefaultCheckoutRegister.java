/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout.internal;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.addons.model.AddonGroupDefinition;
import org.mayocat.addons.util.AddonUtils;
import org.mayocat.configuration.PlatformSettings;
import org.mayocat.context.WebContext;
import org.mayocat.model.AddonGroup;
import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.AddressStore;
import org.mayocat.shop.billing.store.CustomerStore;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.checkout.CheckoutException;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CheckoutResponse;
import org.mayocat.shop.checkout.CheckoutSettings;
import org.mayocat.shop.checkout.RegularCheckoutException;
import org.mayocat.shop.checkout.front.CheckoutResource;
import org.mayocat.shop.payment.BasePaymentData;
import org.mayocat.shop.payment.GatewayException;import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.GatewayResponse;
import org.mayocat.shop.payment.PaymentData;
import org.mayocat.shop.payment.PaymentGateway;
import org.mayocat.shop.payment.event.PaymentOperationEvent;
import org.mayocat.shop.payment.model.PaymentOperation;
import org.mayocat.shop.payment.store.PaymentOperationStore;
import org.mayocat.shop.shipping.ShippingService;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.ObservationManager;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
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
    private PlatformSettings platformSettings;

    @Inject
    private CheckoutSettings checkoutSettings;

    @Inject
    private Provider<OrderStore> orderStore;

    @Inject
    private Provider<CustomerStore> customerStore;

    @Inject
    private Provider<AddressStore> addressStore;

    @Inject
    private Map<String, GatewayFactory> gatewayFactories;

    @Inject
    private Provider<PaymentOperationStore> paymentOperationStore;

    @Inject
    private ShippingService shippingService;

    @Inject
    private ObservationManager observationManager;

    @Inject
    private CartAccessor cartAccessor;

    @Inject
    private WebContext webContext;

    @Override
    public CheckoutResponse checkout(final Cart cart, Customer customer, Address deliveryAddress,
            Address billingAddress, Map<String, Object> extraOrderData) throws CheckoutException
    {
        Preconditions.checkNotNull(customer);
        Order order;
        Customer actualCustomer;

        try {
            UUID customerId;
            UUID deliveryAddressId = null;
            UUID billingAddressId = null;
            Map<String, Object> data = Maps.newHashMap(); // Order JSON data

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

            order = new Order();
            order.setBillingAddressId(billingAddressId);
            order.setDeliveryAddressId(deliveryAddressId);
            order.setCustomerId(customerId);
            if (extraOrderData.containsKey("additionalInformation")) {
                order.setAdditionalInformation((String) extraOrderData.get("additionalInformation"));
            }

            // Items
            Long numberOfItems = 0l;
            final Map<Purchasable, Long> items = cart.getItems();
            List<Map<String, Object>> orderItems = Lists.newArrayList();

            for (final Purchasable p : items.keySet()) {
                numberOfItems += items.get(p);
                BigDecimal unitPrice = p.getUnitPrice();

                if (unitPrice == null && p.getParent().isPresent() && p.getParent().get().isLoaded()) {
                    unitPrice = p.getParent().get().get().getUnitPrice();
                }
                if (unitPrice == null) {
                    throw new RuntimeException("Can't checkout this item");
                }

                String title;
                if (p.getParent().isPresent() && p.getParent().get().isLoaded()) {
                    title = p.getParent().get().get().getTitle() + " - " + p.getTitle();
                } else {
                    title = p.getTitle();
                }

                final String itemTitle = title;
                final BigDecimal price = unitPrice;

                Map<String, Object> itemData = new HashMap<String, Object>()
                {
                    {
                        put("type", "product");
                        put("id", p.getId());
                        put("title", itemTitle);
                        put("quantity", items.get(p));
                        put("unitPrice", price);
                        put("itemTotal", price.multiply(BigDecimal.valueOf(items.get(p))));
                    }
                };

                if (Product.class.isAssignableFrom(p.getClass())) {
                    Product product = (Product) p;

                    if (product.getAddons().isLoaded()) {
                        List<Map<String, Object>> itemAddons = Lists.newArrayList();

                        Map<String, AddonGroup> addons = product.getAddons().get();
                        for (String groupName : addons.keySet()) {
                            AddonGroup addonGroup = addons.get(groupName);
                            Optional<AddonGroupDefinition> definition = AddonUtils
                                    .findAddonGroupDefinition(addonGroup.getGroup(), platformSettings.getAddons(),
                                            webContext.getTheme().getDefinition().getAddons());

                            if (definition.isPresent() &&
                                    definition.get().getProperties().containsKey("checkout.includeInOrder"))
                            {

                                Object value = addonGroup.getValue();
                                if (Map.class.isAssignableFrom(value.getClass())) {

                                    // For now only treat non-repeatable groups

                                    Map<String, Object> valueMap = (Map<String, Object>) value;

                                    for (String key : definition.get().getFields().keySet()) {
                                        String fieldValue = valueMap.get(key).toString();
                                        Map<String, Object> addonMap = Maps.newHashMap();
                                        addonMap.put("value", fieldValue);
                                        addonMap.put("name", key);
                                        addonMap.put("group", addonGroup.getGroup());
                                        itemAddons.add(addonMap);
                                    }
                                }
                            }
                        }

                        if (!itemAddons.isEmpty()) {
                            itemData.put("addons", itemAddons);
                        }
                    }
                }
                orderItems.add(itemData);
            }

            order.setNumberOfItems(numberOfItems);
            order.setItemsTotal(cart.getItemsTotal());

            data.put(Order.ORDER_DATA_ITEMS, orderItems);

            // Shipping
            if (cart.getSelectedShippingOption() != null) {
                final Carrier carrier = shippingService.getCarrier(cart.getSelectedShippingOption().getCarrierId());
                order.setShipping(cart.getSelectedShippingOption().getPrice());
                data.put(Order.ORDER_DATA_SHIPPING, new HashMap<String, Object>()
                {
                    {
                        put("carrierId", carrier.getId());
                        put("title", carrier.getTitle());
                        put("strategy", carrier.getStrategy());
                    }
                });
            }

            // Dates, currency, status
            order.setCreationDate(new Date());
            order.setUpdateDate(order.getCreationDate());
            order.setCurrency(cart.getCurrency());
            order.setStatus(Order.Status.NONE);

            // Grand total
            order.setGrandTotal(cart.getTotal());

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

        String localePath = "";
        if (webContext.isAlternativeLocale()) {
            localePath += webContext.getLocale() + "/";
        }

        Map<PaymentData, Object> options = Maps.newHashMap();
        String baseUri = webContext.getRequest().getBaseUri().toString() + localePath;
        options.put(BasePaymentData.BASE_URL, baseUri);
        options.put(BasePaymentData.CANCEL_URL, baseUri + CheckoutResource.PATH + "/" + order.getId() + "/"
                + CheckoutResource.PAYMENT_CANCEL_PATH);
        options.put(BasePaymentData.RETURN_URL, baseUri + CheckoutResource.PATH + "/"
                + CheckoutResource.PAYMENT_RETURN_PATH + "/" + order.getId());
        options.put(BasePaymentData.CURRENCY, cart.getCurrency());
        options.put(BasePaymentData.ORDER_ID, order.getId());
        options.put(BasePaymentData.CUSTOMER, actualCustomer);

        try {
            CheckoutResponse response = new CheckoutResponse();
            response.setOrder(order);
            GatewayResponse gatewayResponse = gateway.purchase(cart.getTotal(), options);

            if (gatewayResponse.isSuccessful()) {

                if (gatewayResponse.isRedirection()) {
                    response.setRedirectURL(Optional.fromNullable(gatewayResponse.getRedirectURL()));
                }

                cart.empty();
                cartAccessor.setCart(cart);

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
