/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout.front;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.apache.commons.lang3.StringUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.MultitenancySettings;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.context.WebContext;
import org.mayocat.rest.Resource;
import org.mayocat.rest.annotation.ExistingTenant;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.shop.checkout.CheckoutException;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CheckoutRequest;
import org.mayocat.shop.checkout.CheckoutResponse;
import org.mayocat.shop.checkout.CheckoutSettings;
import org.mayocat.shop.checkout.RegularCheckoutException;
import org.mayocat.shop.checkout.internal.CheckoutRequestBuilder;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;
import org.mayocat.shop.customer.store.AddressStore;
import org.mayocat.shop.customer.store.CustomerStore;
import org.mayocat.shop.front.views.WebView;
import org.mayocat.shop.payment.BasePaymentData;
import org.mayocat.shop.payment.CreditCardPaymentData;
import org.mayocat.shop.payment.PaymentData;
import org.mayocat.shop.payment.PaymentProcessor;
import org.mayocat.shop.payment.PaymentRequest;
import org.mayocat.shop.payment.PaymentStatus;
import org.mayocat.shop.payment.RequiredAction;
import org.mayocat.url.URLHelper;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component(CheckoutResource.PATH)
@Path(CheckoutResource.PATH)
@Produces({MediaType.TEXT_HTML, MediaType.APPLICATION_JSON})
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CheckoutResource implements Resource
{
    public static final String PATH = "checkout";

    public static final String PAYMENT_RETURN_PATH = "return";

    public static final String PAYMENT_CANCEL_PATH = "cancel";

    @Inject
    private Provider<CustomerStore> customerStore;

    @Inject
    private Provider<AddressStore> addressStore;

    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private CheckoutRegister checkoutRegister;

    @Inject
    private SiteSettings siteSettings;

    @Inject
    private URLHelper urlHelper;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private Provider<OrderStore> orderStore;

    @Inject
    private WebContext webContext;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private PaymentProcessor paymentProcessor;

    @Inject
    private Logger logger;

    @POST
    public Object checkout(final MultivaluedMap data) {

        CheckoutSettings checkoutSettings = configurationService.getSettings(CheckoutSettings.class);
        if (webContext.getUser() == null && !checkoutSettings.isGuestCheckoutEnabled().getValue()) {
            return Response.status(Response.Status.FORBIDDEN).
                    entity("Configuration does not allow guest checkout").build();
        }

        CheckoutRequest checkoutRequest = null;
        if (webContext.getUser() != null) {
            Customer customer = customerStore.get().findByUserId(webContext.getUser().getId());
            if (customer != null) {
                Address deliveryAddress = addressStore.get().findByCustomerIdAndType(customer.getId(), "delivery");
                Address billingAddress = addressStore.get().findByCustomerIdAndType(customer.getId(), "billing");
                checkoutRequest = new CheckoutRequest();
                checkoutRequest.setCustomer(customer);
                checkoutRequest.setBillingAddress(billingAddress);
                checkoutRequest.setDeliveryAddress(deliveryAddress);
            }
        }
        if (checkoutRequest == null) {
            CheckoutRequestBuilder builder = new CheckoutRequestBuilder();
            checkoutRequest = builder.build(data);
        }

        if (checkoutRequest.getErrors().size() != 0) {
            Map<String, Object> bindings = new HashMap<>();

            bindings.put("request", data);
            bindings.put("errors", checkoutRequest.getErrors());
            return new WebView().template("checkout/form.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                    .data(bindings);
        }

        try {
            CheckoutResponse response;
            if (data.containsKey("product")) {
                // Direct checkout
                Product product = productStore.get().findBySlug((String) data.getFirst("product"));

                if (product == null) {
                    return Response.status(Response.Status.BAD_REQUEST).entity("Direct checkout product not found").build();
                }

                Long quantity;
                if (data.containsKey("quantity")) {
                    try {
                        quantity = Long.parseLong((String) data.getFirst("quantity"));
                    } catch (NumberFormatException e) {
                        return Response.status(Response.Status.BAD_REQUEST).entity("Malformed quantity.").build();
                    }
                } else {
                    quantity = 1l;
                }
                response = checkoutRegister.directCheckout(checkoutRequest, product, quantity);

            } else {
                // Cart checkout
                response = checkoutRegister.checkoutCart(checkoutRequest);
            }

            return generateCheckoutResponse(response);
        } catch (final Exception e) {
            this.logger.error("Failed to checkout", e);
            return renderError(e.getMessage());
        }
    }

    private Object generateCheckoutResponse(CheckoutResponse response) throws URISyntaxException {
        PaymentRequest paymentRequest = response.getPaymentRequest();
        Map<String, Object> bindings = new HashMap<>();

        switch(paymentRequest.getNextAction()) {
            case GET_EXTERNAL_URL:
                return Response.seeOther(new URI(paymentRequest.getRedirectionTarget().get())).build();
            case POST_EXTERNAL_URL:
            case INTERNAL_FORM:
                bindings.put("formURL", paymentRequest.getRedirectionTarget().isPresent()
                        ? paymentRequest.getRedirectionTarget().get()
                        :"/checkout/" + response.getOrder().getId() + "/payment");
                bindings.put("paymentData", paymentRequest.getData());

                // Also add the payment data keys in the response. This can be useful for JSON clients
                // Since keys are not ordered in JSON, objects keys are not ordered, and for some
                // payment gateways, respecting the order of keys is mandatory.
                bindings.put("paymentDataKeys", paymentRequest.getData().keySet());
                return new WebView().template("checkout/payment.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                        .data(bindings);

            case MANUAL_VALIDATION:
            case NONE:
            default:
                bindings.putAll(
                        prepareContext(
                                response.getOrder(),
                                response.getOrder().getCustomer(),
                                Optional.fromNullable(response.getOrder().getBillingAddress()),
                                Optional.fromNullable(response.getOrder().getDeliveryAddress()),
                                webContext.getTenant(),
                                configurationService.getSettings(GeneralSettings.class).getLocales().getMainLocale().getValue()
                        )
                );

                String template = "checkout/success.html";
                if (paymentRequest.getNextAction().equals(RequiredAction.MANUAL_VALIDATION)) {
                    template = "checkout/pending.html";
                } else if (paymentRequest.getStatus().equals(PaymentStatus.FAILED)
                    || paymentRequest.getStatus().equals(PaymentStatus.REFUSED)) {
                    template = "checkout/failed.html";
                }

                return new WebView().template(template).with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                        .data(bindings);
        }
    }

    @GET
    public Object checkout() {
            Map<String, Object> bindings = new HashMap<>();
            return new WebView().template("checkout/form.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                    .data(bindings);
    }

    @POST
    @Path("{orderId}/payment")
    public Object internalPayment(@PathParam("orderId") UUID orderId, MultivaluedMap<String, String> data) {

        try {
            Order order = orderStore.get().findById(orderId);
            Map<PaymentData, Object> paymentData = Maps.newHashMap();
            paymentData.put(BasePaymentData.CURRENCY, order.getCurrency());
            paymentData.put(BasePaymentData.ORDER_ID, order.getId());
            paymentData.put(BasePaymentData.CUSTOMER, order.getCustomer());
            if (order.getBillingAddress() != null) {
                paymentData.put(BasePaymentData.BILLING_ADDRESS, order.getBillingAddress());
            }
            paymentData.put(BasePaymentData.DELIVERY_ADDRESS, order.getDeliveryAddress());
            paymentData.put(BasePaymentData.ORDER, order);

            if (data.containsKey("cardNumber")) {
                paymentData.put(CreditCardPaymentData.CARD_NUMBER, data.getFirst("cardNumber"));
                paymentData.put(CreditCardPaymentData.HOLDER_NAME, data.getFirst("holderName"));
                paymentData.put(CreditCardPaymentData.EXPIRATION_MONTH, data.getFirst("expiryMonth"));
                paymentData.put(CreditCardPaymentData.EXPIRATION_YEAR, data.getFirst("expiryYear"));
                paymentData.put(CreditCardPaymentData.VERIFICATION_CODE, data.getFirst("cvv"));
            }

            PaymentRequest paymentRequest = paymentProcessor.requestPayment(order, paymentData);
            CheckoutResponse response = new CheckoutResponse(order, paymentRequest);

            return generateCheckoutResponse(response);
        } catch (Exception e) {
            return renderError(e.getMessage());
        }
    }

    @GET
    @Path(PAYMENT_RETURN_PATH + "/{order}/payment")
    public WebView returnFromPaymentService(@Context UriInfo uriInfo, @PathParam("order") String orderId) {
        Map<String, Object> bindings = new HashMap<>();
        if (StringUtils.isNotBlank(orderId)) {
            Order order = orderStore.get().findById(UUID.fromString(orderId));
            if (order != null) {
                Optional<Address> da = order.getDeliveryAddress() != null ?
                        Optional.fromNullable(order.getDeliveryAddress()) : Optional.<Address>absent();
                Optional<Address> ba = order.getBillingAddress() != null ?
                        Optional.fromNullable(order.getBillingAddress()) : Optional.<Address>absent();
                bindings.putAll(
                        prepareContext(order, order.getCustomer(), ba, da, webContext.getTenant(),
                                configurationService.getSettings(GeneralSettings.class).getLocales().getMainLocale()
                                        .getValue()));
            }
        }
        return new WebView().template("checkout/success.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                .data(bindings);
    }

    @POST
    @Path(PAYMENT_RETURN_PATH + "/{order}")
    public WebView postReturnFromPaymentService(@Context UriInfo uriInfo, @PathParam("order") String orderId) {
        return this.returnFromPaymentService(uriInfo, orderId);
    }

    @GET
    @Path(PAYMENT_CANCEL_PATH + "/{orderId}")
    public WebView cancelFromPaymentService(@PathParam("orderId") UUID orderId) {
        try {
            checkoutRegister.dropOrder(orderId);
        } catch (final CheckoutException e) {
            if (!RegularCheckoutException.class.isAssignableFrom(e.getClass())) {
                // If this is not a regular checkout exception (like for example: order has already been deleted),
                // we need to take additional measures
                this.logger.error("Failed to cancel order", e);
            }

            Map<String, Object> bindings = new HashMap<>();
            bindings.put("exception", new HashMap<String, Object>()
            {
                {
                    put("message", e.getMessage());
                }
            });
            return new WebView().template("checkout/exception.html").with(
                    WebView.Option.FALLBACK_ON_DEFAULT_THEME).data(bindings);
        }
        Map<String, Object> bindings = new HashMap<>();
        return new WebView().template("checkout/cancelled.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                .data(bindings);
    }

    @POST
    @Path(PAYMENT_CANCEL_PATH + "/{orderId}")
    public WebView postCancelFromPaymentService(@PathParam("orderId") UUID orderId) {
        return this.cancelFromPaymentService(orderId);
    }

    /**
     * Render a user facing error page
     *
     * @param message the message associated with the error
     * @return the error page
     */
    private Object renderError(final String message) {
        Map<String, Object> bindings = new HashMap<>();
        bindings.put("exception", new HashMap<String, Object>()
        {
            {
                put("message", message);
            }
        });
        return new WebView().template("checkout/exception.html").with(
                WebView.Option.FALLBACK_ON_DEFAULT_THEME).data(bindings);
    }

    /**
     * Prepare checkout response context
     *
     * @param order the order concerned by the notification
     * @param customer the customer
     * @param ba an optional billing address
     * @param da an optional shipping address
     * @param tenant the tenant the order was checked out from
     * @param locale the main locale of the tenant
     * @return a JSON context as map
     */
    private Map<String, Object> prepareContext(Order order, Customer customer, Optional<Address> ba,
                                               Optional<Address> da, Tenant tenant, Locale locale) {
        List<OrderItem> items = order.getOrderItems();
        Map<String, Object> context = Maps.newHashMap();

        MoneyFormatter formatter = new MoneyFormatterBuilder().appendAmount(MoneyAmountStyle.of(locale))
                .appendLiteral(" ")
                .appendCurrencySymbolLocalized()
                .toFormatter();

        CurrencyUnit currencyUnit = CurrencyUnit.of(order.getCurrency());
        String grandTotal = formatter.withLocale(locale)
                .print(Money.of(currencyUnit, order.getGrandTotal(), RoundingMode.HALF_EVEN));
        String itemsTotal = formatter.withLocale(locale)
                .print(Money.of(currencyUnit, order.getItemsTotal(), RoundingMode.HALF_EVEN));

        List<Map<String, Object>> orderItems = Lists.newArrayList();
        for (OrderItem item : items) {
            Map<String, Object> orderItem = Maps.newHashMap();
            orderItem.put("title", item.getTitle());
            orderItem.put("quantity", item.getQuantity());

            // Replace big decimal values by formatted values
            Double unitPrice = item.getUnitPrice().doubleValue();
            Double itemTotal = item.getItemTotal().doubleValue();

            orderItem.put("unitPrice", formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, unitPrice, RoundingMode.HALF_EVEN)));
            orderItem.put("itemTotal", formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, itemTotal, RoundingMode.HALF_EVEN)));

            orderItems.add(orderItem);
        }

        context.put("items", orderItems);

        if (order.getShipping() != null) {
            String shippingTotal = formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, order.getShipping(), RoundingMode.HALF_EVEN));
            context.put("shippingTotal", shippingTotal);
            context.put("shipping", order.getOrderData().get("shipping"));
        }

        context.put("siteName", tenant.getName());
        context.put("itemsTotal", itemsTotal);
        context.put("orderId", order.getSlug());
        context.put("grandTotal", grandTotal);

        Map<String, Object> customerMap = Maps.newHashMap();
        customerMap.put("firstName", customer.getFirstName());
        customerMap.put("lastName", customer.getLastName());
        customerMap.put("email", customer.getEmail());
        customerMap.put("phone", customer.getPhoneNumber());
        context.put("customer", customerMap);

        if (ba.isPresent()) {
            context.put("billingAddress", prepareAddressContext(ba.get()));
        }
        if (da.isPresent()) {
            context.put("deliveryAddress", prepareAddressContext(da.get()));
        }

        context.put("siteUrl", urlHelper.getContextWebURL("").toString());

        return context;
    }

    /**
     * Prepares the context for an address
     *
     * @param address the address to get the context of
     * @return the prepared context
     */
    private Map<String, Object> prepareAddressContext(Address address) {
        Map<String, Object> addressContext = Maps.newHashMap();
        addressContext.put("street", address.getStreet());
        addressContext.put("streetComplement", address.getStreetComplement());
        addressContext.put("zip", address.getZip());
        addressContext.put("city", address.getCity());
        addressContext.put("country", address.getCountry());
        addressContext.put("company", address.getCompany());
        return addressContext;
    }
}

