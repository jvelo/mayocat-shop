/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.checkout.front;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;
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
import org.apache.commons.validator.routines.EmailValidator;
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
import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.OrderStore;
import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.checkout.CheckoutException;
import org.mayocat.shop.checkout.CheckoutRegister;
import org.mayocat.shop.checkout.CheckoutResponse;
import org.mayocat.shop.checkout.RegularCheckoutException;
import org.mayocat.shop.front.views.WebView;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;

/**
 * @version $Id$
 */
@Component(CheckoutResource.PATH)
@Path(CheckoutResource.PATH)
@Produces({ MediaType.TEXT_HTML, MediaType.APPLICATION_JSON })
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@ExistingTenant
public class CheckoutResource implements Resource
{
    public static final String PATH = "checkout";

    public static final String PAYMENT_RETURN_PATH = "return";

    public static final String PAYMENT_CANCEL_PATH = "cancel";

    @Inject
    private CheckoutRegister checkoutRegister;

    @Inject
    private CartAccessor cartAccessor;

    @Inject
    private SiteSettings siteSettings;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private Provider<OrderStore> orderStore;

    @Inject
    private WebContext webContext;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Logger logger;

    public CheckoutResource()
    {
    }

    private enum ErrorType
    {
        REQUIRED,
        BAD_VALUE;

        @JsonValue
        public String toJson()
        {
            return name().toLowerCase();
        }
    }

    private class Error
    {
        private String userMessage;

        private ErrorType errorType;

        public Error(ErrorType type, String userMessage)
        {
            this.errorType = type;
            this.userMessage = userMessage;
        }

        public String getUserMessage()
        {
            return userMessage;
        }

        public ErrorType getErrorType()
        {
            return errorType;
        }
    }

    @POST
    public Object checkout(final MultivaluedMap data)
    {
        Map<String, Error> errors = Maps.newHashMap();
        String email = null;

        if (data.containsKey("email")) {
            email = (String) data.getFirst("email");
            EmailValidator emailValidator = EmailValidator.getInstance(false);
            if (!emailValidator.isValid(email)) {
                Error error = new Error(ErrorType.BAD_VALUE, "email is not valid");
                errors.put("email", error);
            }
        } else {
            Error error = new Error(ErrorType.REQUIRED, "email is mandatory");
            errors.put("email", error);
        }

        String firstName = getNonEmptyFieldValueOrAddToErrorMap("firstName", data, errors);
        String lastName = getNonEmptyFieldValueOrAddToErrorMap("lastName", data, errors);
        String street = getNonEmptyFieldValueOrAddToErrorMap("street", data, errors);
        String zip = getNonEmptyFieldValueOrAddToErrorMap("zip", data, errors);
        String city = getNonEmptyFieldValueOrAddToErrorMap("city", data, errors);
        String country = getNonEmptyFieldValueOrAddToErrorMap("country", data, errors);

        Address billingAddress = null;
        boolean hasDifferentBillingAddress =
                FluentIterable.from(Arrays.asList("street", "zip", "city", "country")).anyMatch(new Predicate<String>()
                {
                    public boolean apply(@Nullable String input)
                    {
                        return data.containsKey("billing" + StringUtils.capitalize(input)) && StringUtils.isNotBlank(
                                (String) data.getFirst("billing" + StringUtils.capitalize(input)));
                    }
                });
        if (hasDifferentBillingAddress) {
            String billingStreet = getNonEmptyFieldValueOrAddToErrorMap("billingStreet", data, errors);
            String billingZip = getNonEmptyFieldValueOrAddToErrorMap("billingZip", data, errors);
            String billingCity = getNonEmptyFieldValueOrAddToErrorMap("billingCity", data, errors);
            String billingCountry = getNonEmptyFieldValueOrAddToErrorMap("billingCountry", data, errors);
            billingAddress = new Address();
            billingAddress.setFullName(firstName + " " + lastName);
            billingAddress.setStreet(billingStreet);
            billingAddress.setZip(billingZip);
            billingAddress.setCity(billingCity);
            billingAddress.setCountry(billingCountry);
            if (data.containsKey("billingCompany")) {
                String company = (String) data.getFirst("billingCompany");
                if (!Strings.isNullOrEmpty(company)) {
                    billingAddress.setCompany(company);
                }
                String streetComplement = (String) data.getFirst("billingStreetComplement");
                if (!Strings.isNullOrEmpty(streetComplement)) {
                    billingAddress.setStreetComplement(streetComplement);
                }
            }
        }

        if (errors.keySet().size() > 0) {
            Map<String, Object> bindings = new HashMap<>();

            bindings.put("request", data);
            bindings.put("errors", errors);

            return new WebView().template("checkout/form.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                    .data(bindings);
        }

        Customer customer = new Customer();
        customer.setEmail(email);
        customer.setFirstName(firstName);
        customer.setLastName(lastName);
        if (data.containsKey("phone") && !Strings.isNullOrEmpty((String) data.getFirst("phone"))) {
            customer.setPhoneNumber((String) data.getFirst("phone"));
        }

        Address deliveryAddress = new Address();
        deliveryAddress.setFullName(firstName + " " + lastName);
        deliveryAddress.setStreet(street);
        deliveryAddress.setZip(zip);
        deliveryAddress.setCity(city);
        deliveryAddress.setCountry(country);
        if (data.containsKey("company")) {
            String company = (String) data.getFirst("company");
            if (!Strings.isNullOrEmpty(company)) {
                deliveryAddress.setCompany(company);
            }
            String streetComplement = (String) data.getFirst("streetComplement");
            if (!Strings.isNullOrEmpty(streetComplement)) {
                deliveryAddress.setStreetComplement(streetComplement);
            }
        }

        Map<String, Object> otherOrderData = Maps.newHashMap();

        // Include additional information if the field is present and not empty
        if (data.containsKey("additionalInformation") &&
                !Strings.isNullOrEmpty((String) data.getFirst("additionalInformation")))
        {
            otherOrderData.put("additionalInformation", data.getFirst("additionalInformation"));
        }

        try {
            Cart cart = cartAccessor.getCart();
            CheckoutResponse response =
                    checkoutRegister.checkout(cart, customer, deliveryAddress, billingAddress, otherOrderData);
            if (response.getRedirectURL().isPresent()) {
                return Response.seeOther(new URI(response.getRedirectURL().get())).build();
            } else {
                Map<String, Object> bindings = new HashMap<>();
                bindings.put("errors", errors);
                bindings.putAll(prepareMailContext(response.getOrder(), customer, Optional.fromNullable(billingAddress),
                        Optional.fromNullable(deliveryAddress), webContext.getTenant(),
                        configurationService.getSettings(
                                GeneralSettings.class).getLocales().getMainLocale().getValue()));
                return new WebView().template("checkout/success.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                        .data(bindings);
            }
        } catch (final Exception e) {
            this.logger.error("Exception checking out", e);
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
    }

    @GET
    public Object checkout()
    {
        if (checkoutRegister.requiresForm()) {
            Map<String, Object> bindings = new HashMap<>();
            return new WebView().template("checkout/form.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                    .data(bindings);
        } else {
            try {
                Cart cart = cartAccessor.getCart();
                CheckoutResponse response =
                        checkoutRegister.checkout(cart, null, null, null, Maps.<String, Object>newHashMap());

                if (response.getRedirectURL().isPresent()) {
                    return Response.seeOther(new URI(response.getRedirectURL().get())).build();
                }
            } catch (final CheckoutException e) {
                Map<String, Object> bindings = new HashMap<>();
                bindings.put("exception", new HashMap<String, Object>()
                {
                    {
                        put("message", e.getMessage());
                    }
                });
                return new WebView().template("checkout/exception.html")
                        .with(WebView.Option.FALLBACK_ON_DEFAULT_THEME).data(bindings);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return Response.ok().build();
    }

    @GET
    @Path(PAYMENT_RETURN_PATH + "/{order}")
    public WebView returnFromExternalPaymentService(@Context UriInfo uriInfo, @PathParam("order") String orderId)
    {
        Map<String, Object> bindings = new HashMap<>();
        if (StringUtils.isNotBlank(orderId)) {
            Order order = orderStore.get().findById(UUID.fromString(orderId));
            if (order != null) {
                Optional<Address> da = order.getDeliveryAddress().isLoaded() ?
                        Optional.fromNullable(order.getDeliveryAddress().get()) : Optional.<Address>absent();
                Optional<Address> ba = order.getBillingAddress().isLoaded() ?
                        Optional.fromNullable(order.getBillingAddress().get()) : Optional.<Address>absent();
                bindings.putAll(
                        prepareMailContext(order, order.getCustomer().get(), ba, da, webContext.getTenant(),
                                configurationService.getSettings(GeneralSettings.class).getLocales().getMainLocale()
                                        .getValue()));
            }
        }
        return new WebView().template("checkout/success.html").with(WebView.Option.FALLBACK_ON_DEFAULT_THEME)
                .data(bindings);
    }

    @GET
    @Path("{orderId}/" + PAYMENT_CANCEL_PATH)
    public WebView cancelFromExternalPaymentService(@PathParam("orderId") UUID orderId)
    {
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

    private String getNonEmptyFieldValueOrAddToErrorMap(String field, MultivaluedMap data, Map<String, Error> errors)
    {
        if (!data.containsKey(field) || Strings.isNullOrEmpty((String) data.getFirst(field))) {
            Error error = new Error(ErrorType.REQUIRED, StringUtils.capitalize(field) + " is mandatory");
            errors.put(field, error);
            return null;
        } else {
            return (String) data.getFirst(field);
        }
    }

    /**
     * Prepares the JSON context for an order mail notification
     *
     * @param order the order concerned by the notification
     * @param customer the customer
     * @param ba an optional billing address
     * @param da an optional shipping address
     * @param tenant the tenant the order was checked out from
     * @param locale the main locale of the tenant
     * @return a JSON context as map
     */
    private Map<String, Object> prepareMailContext(Order order, Customer customer, Optional<Address> ba,
            Optional<Address> da, Tenant tenant, Locale locale)
    {
        Map<String, Object> orderData = order.getOrderData();
        List<Map<String, Object>> items = (List<Map<String, Object>>) orderData.get("items");
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

        for (Map<String, Object> item : items) {
            // Replace big decimal values by formatted values
            Double unitPrice;
            Double itemTotal;
            if (BigDecimal.class.isAssignableFrom(item.get("unitPrice").getClass())) {
                unitPrice = ((BigDecimal) item.get("unitPrice")).doubleValue();
                itemTotal = ((BigDecimal) item.get("itemTotal")).doubleValue();
            } else {
                unitPrice = (Double) item.get("unitPrice");
                itemTotal = (Double) item.get("itemTotal");
            }

            item.put("unitPrice", formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, unitPrice, RoundingMode.HALF_EVEN)));
            item.put("itemTotal", formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, itemTotal, RoundingMode.HALF_EVEN)));
        }

        context.put("items", items);

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

        context.put("siteUrl", getSiteUrl(tenant));

        return context;
    }

    /**
     * Prepares the context for an address
     *
     * @param address the address to get the context of
     * @return the prepared context
     */
    private Map<String, Object> prepareAddressContext(Address address)
    {
        Map<String, Object> addressContext = Maps.newHashMap();
        addressContext.put("street", address.getStreet());
        addressContext.put("streetComplement", address.getStreetComplement());
        addressContext.put("zip", address.getZip());
        addressContext.put("city", address.getCity());
        addressContext.put("country", address.getCountry());
        addressContext.put("company", address.getCompany());
        return addressContext;
    }

    /**
     * Returns the site URL for a tenant
     *
     * @param tenant the tenant to get the site URL from
     * @return the site URL for the tenant
     */
    private String getSiteUrl(Tenant tenant)
    {
        if (multitenancySettings.isActivated()) {
            return "http://" + tenant.getSlug() + "." + siteSettings.getDomainName();
        } else {
            return "http://" + siteSettings.getDomainName();
        }
    }
}

