/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.internal;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
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
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailTemplate;
import org.mayocat.mail.MailTemplateService;
import org.mayocat.shop.billing.event.OrderPaidEvent;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.customer.model.Address;
import org.mayocat.shop.customer.model.Customer;
import org.mayocat.shop.customer.store.AddressStore;
import org.mayocat.shop.customer.store.CustomerStore;
import org.mayocat.url.URLHelper;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Event listener that sends email to tenant and customer when an order is paid.
 *
 * The mails are sent asynchronously from a thread spawned just for that purpose, so the event returns immediately.
 *
 * @version $Id$
 */
@Component("paidItemsEmailSendingEventListener")
public class SendEmailsWhenOrderIsPaid implements EventListener
{
    @Inject
    private MailTemplateService mailTemplateService;

    @Inject
    private Logger logger;

    @Inject
    private WebContext webContext;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private SiteSettings siteSettings;

    @Inject
    private URLHelper urlHelper;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Provider<CustomerStore> customerStore;

    @Inject
    private Provider<AddressStore> addressStore;

    /**
     * @see {@link org.xwiki.observation.EventListener#getName()}
     */
    public String getName()
    {
        return "paidItemsEmailSendingEventListener";
    }

    /**
     * @see {@link org.xwiki.observation.EventListener#getEvents()}
     */
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new OrderPaidEvent());
    }

    /**
     * @see {@link org.xwiki.observation.EventListener#onEvent(org.xwiki.observation.event.Event, Object, Object)}
     */
    public void onEvent(Event event, Object source, Object data)
    {
        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class);
        final Order order = (Order) source;
        final Locale customerLocale = webContext.getLocale();
        final Tenant tenant = webContext.getTenant();
        final Locale tenantLocale = settings.getLocales().getMainLocale().getValue();

        Executors.newSingleThreadExecutor().submit(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                sendNotificationMails(order, tenant, customerLocale, tenantLocale);
                return null;
            }
        });
    }

    /**
     * Sends the actual notification mails.
     *
     * Warning: Since this is done in a spawned thread, so we can't use the web context thread local data from there
     *
     * @param order the order concerned by the notifications mails
     * @param tenant the tenant concerned by the notifications mails
     * @param customerLocale the locale the customer browsed the site in when checking out
     * @param tenantLocale the main locale of the tenant
     */
    private void sendNotificationMails(Order order, Tenant tenant, Locale customerLocale, Locale tenantLocale)
    {
        try {
            Customer customer = order.getCustomer();

            Optional<Address> billingAddress;
            if (order.getBillingAddress() != null) {
                billingAddress = Optional.of(order.getBillingAddress());
            } else {
                billingAddress = Optional.absent();
            }
            Optional<Address> deliveryAddress;
            if (order.getDeliveryAddress() != null) {
                deliveryAddress = Optional.of(order.getDeliveryAddress());
            } else {
                deliveryAddress = Optional.absent();
            }

            Map<String, Object> emailContext =
                    prepareMailContext(order, customer, billingAddress, deliveryAddress, tenant, tenantLocale);

            String customerEmail = customer.getEmail();

            MailTemplate customerNotificationEmail = getCustomerNotificationEmail(tenant,
                    customerEmail, customerLocale);
            sendNotificationMail(customerNotificationEmail, emailContext);
            MailTemplate tenantNotificationEmail = getTenantNotificationEmail(tenant, tenantLocale);
            sendNotificationMail(tenantNotificationEmail, emailContext);
        } catch (Exception e) {
            logger.error("Failed to send order notification email when sending email", e);
        }
    }

    /**
     * @param tenant the tenant of the shop the customer checked out from
     * @param customerEmail the email of the customer
     * @param locale the locale of the customer
     * @return an optional notification email object, present if all information is there and valid and the template
     * exists, absent otherwise
     */
    private MailTemplate getCustomerNotificationEmail(Tenant tenant,
            String customerEmail, Locale locale)
    {
        return new MailTemplate()
                .from(getTenantContactEmail(tenant))
                .to(customerEmail)
                .template("order-paid-customer")
                .locale(locale);
    }

    /**
     * @param tenant the tenant of the shop the customer checked out from
     * @param locale the main locale of the tenant
     * @return an optional notification email object, present if all information is there and valid and the template
     * exists, absent otherwise
     */
    private MailTemplate getTenantNotificationEmail(Tenant tenant, Locale locale)
    {
        return new MailTemplate()
                .from(generalSettings.getNotificationsEmail())
                .to(getTenantContactEmail(tenant))
                .template("order-paid-tenant")
                .locale(locale);
    }

    /**
     * Retrieves the contact email for a tenant. If the tenant is null ("global tenant"), then use the global site
     * settings
     *
     * @param tenant the tenant for which to get the contact email, or null for a global tenant
     * @return the found contact email or null if no contact email has been found
     */
    private String getTenantContactEmail(Tenant tenant)
    {
        if (tenant == null) {
            return siteSettings.getContactEmail();
        } else {
            return StringUtils.isNotBlank(tenant.getContactEmail()) ?
                    tenant.getContactEmail() :
                    generalSettings.getNotificationsEmail();
        }
    }

    /**
     * Actually sends a notification email
     *
     * @param template the mail template
     * @param context the mail JSON context
     */
    private void sendNotificationMail(MailTemplate template, Map<String, Object> context)
    {
        try {
            mailTemplateService.sendTemplateMail(template, context);
        } catch (MailException e) {
            logger.error("Failed to send order paid email", ExceptionUtils.getRootCause(e));
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

        List<Map<String, Object>> itemsData = Lists.newArrayList();

        for (OrderItem item : items) {
            // Replace big decimal values by formatted values
            Map<String, Object> itemData = Maps.newHashMap();
            itemData.put("unitPrice", formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, item.getUnitPrice(), RoundingMode.HALF_EVEN)));
            itemData.put("itemTotal", formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, item.getItemTotal(), RoundingMode.HALF_EVEN)));
            itemData.put("title", item.getTitle());
            itemData.put("quantity", item.getQuantity());
            itemData.put("vatRate", item.getVatRate().longValue());
            itemsData.add(itemData);
        }

        context.put("items", itemsData);

        if (order.getShipping() != null) {
            String shippingTotal = formatter.withLocale(locale)
                    .print(Money.of(currencyUnit, order.getShipping(), RoundingMode.HALF_EVEN));
            context.put("shippingTotal", shippingTotal);
            context.put("shipping", order.getOrderData().get("shipping"));
        }

        context.put("siteName", tenant != null ? tenant.getName() : siteSettings.getName());
        context.put("itemsTotal", itemsTotal);
        context.put("orderId", order.getSlug());
        context.put("grandTotal", grandTotal);
        context.put("additionalInformation", order.getAdditionalInformation());

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

        context.put("siteUrl", urlHelper.getTenantWebURL(tenant, "").toString());
        context.put("backOfficeUrl", urlHelper.getTenantBackOfficeURL(tenant, "").toString());

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
}
