/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.billing.internal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.io.IOUtils;
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
import org.mayocat.files.FileManager;
import org.mayocat.mail.Mail;
import org.mayocat.mail.MailException;
import org.mayocat.mail.MailService;
import org.mayocat.shop.billing.event.OrderPaidEvent;
import org.mayocat.shop.billing.model.Address;
import org.mayocat.shop.billing.model.Customer;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.store.AddressStore;
import org.mayocat.shop.billing.store.CustomerStore;
import org.mayocat.views.Template;
import org.mayocat.views.TemplateEngine;
import org.mayocat.views.TemplateEngineException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

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
    private MailService emailService;

    @Inject
    private Provider<TemplateEngine> engine;

    @Inject
    private FileManager fileManager;

    @Inject
    private Logger logger;

    @Inject
    private WebContext webContext;

    @Inject
    private GeneralSettings generalSettings;

    @Inject
    private SiteSettings siteSettings;

    @Inject
    private MultitenancySettings multitenancySettings;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Provider<CustomerStore> customerStore;

    @Inject
    private Provider<AddressStore> addressStore;

    /**
     * Representation of an order notification, with a sender, a recipient a JSON context (as map) and a mail template.
     */
    private final static class OrderNotificationMail
    {
        private final Template template;

        private final Map<String, Object> context;

        private final String from;

        private final String recipient;

        private OrderNotificationMail(Template template, Map<String, Object> context, String from, String recipient)
        {
            this.template = template;
            this.context = context;
            this.from = from;
            this.recipient = recipient;
        }

        private Template getTemplate()
        {
            return template;
        }

        private Map<String, Object> getContext()
        {
            return context;
        }

        private String getFrom()
        {
            return from;
        }

        private String getRecipient()
        {
            return recipient;
        }
    }

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
        Customer customer = order.getCustomer().get();

        Optional<Address> billingAddress;
        if (order.getBillingAddress().isLoaded()) {
            billingAddress = Optional.of(order.getBillingAddress().get());
        } else {
            billingAddress = Optional.absent();
        }
        Optional<Address> deliveryAddress;
        if (order.getDeliveryAddress().isLoaded()) {
            deliveryAddress = Optional.of(order.getDeliveryAddress().get());
        } else {
            deliveryAddress = Optional.absent();
        }

        Map<String, Object> emailContext =
                prepareMailContext(order, customer, billingAddress, deliveryAddress, tenant, tenantLocale);

        String customerEmail = customer.getEmail();

        Optional<OrderNotificationMail> customerNotificationEmail = getCustomerNotificationEmail(tenant, emailContext,
                customerEmail, customerLocale);
        if (customerNotificationEmail.isPresent()) {
            sendNotificationMail(customerNotificationEmail.get());
        } else {
            logger.warn("Can't send notification email to customer. Does the mail template exists ?");
        }
        Optional<OrderNotificationMail> tenantNotificationEmail =
                getTenantNotificationEmail(tenant, emailContext, tenantLocale);
        if (tenantNotificationEmail.isPresent()) {
            sendNotificationMail(tenantNotificationEmail.get());
        }
    }

    /**
     * @param tenant the tenant of the shop the customer checked out from
     * @param context the JSON context of the notification mail
     * @param customerEmail the email of the customer
     * @param locale the locale of the customer
     * @return an optional notification email object, present if all information is there and valid and the template
     *         exists, absent otherwise
     */
    private Optional<OrderNotificationMail> getCustomerNotificationEmail(Tenant tenant, Map<String, Object> context,
            String customerEmail, Locale locale)
    {
        String from = StringUtils.isNotBlank(tenant.getContactEmail()) ?
                tenant.getContactEmail() :
                generalSettings.getNotificationsEmail();
        Optional<Template> template = getTemplate("order_paid_customer_email.html", locale);
        if (!template.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(new OrderNotificationMail(template.get(), context, from, customerEmail));
    }

    /**
     * @param tenant the tenant of the shop the customer checked out from
     * @param context the JSON context of the notification mail
     * @param locale the main locale of the tenant
     * @return an optional notification email object, present if all information is there and valid and the template
     *         exists, absent otherwise
     */
    private Optional<OrderNotificationMail> getTenantNotificationEmail(Tenant tenant, Map<String, Object> context,
            Locale locale)
    {
        if (StringUtils.isBlank(tenant.getContactEmail())) {
            return Optional.absent();
        }
        Optional<Template> template = getTemplate("order_paid_tenant_email.html", locale);
        if (!template.isPresent()) {
            return Optional.absent();
        }
        return Optional.of(new OrderNotificationMail(template.get(), context,
                generalSettings.getNotificationsEmail(), tenant.getContactEmail()));
    }

    /**
     * Retrieves a notification mail template
     *
     * @param templateName the name of the template to get
     * @param locale the locale to get it in
     * @return an optional template, present if found (might not be in the asked locale, fallback on a global language)
     *         absent otherwise
     */
    private Optional<Template> getTemplate(String templateName, Locale locale)
    {
        // Localized as country + language
        Path templatePath = fileManager.resolvePermanentFilePath(Paths.get("emails").resolve(locale.toLanguageTag()))
                .resolve(templateName);

        // Localized as language
        if (!templatePath.toFile().isFile()) {
            templatePath = fileManager.resolvePermanentFilePath(Paths.get("emails"))
                    .resolve(locale.getLanguage()).resolve(templateName);
        }

        // Global fallback
        if (!templatePath.toFile().isFile()) {
            templatePath =
                    fileManager.resolvePermanentFilePath(Paths.get("emails")).resolve(templateName);
        }

        if (templatePath.toFile().isFile()) {
            try {
                String content = Files.toString(templatePath.toFile(), Charsets.UTF_8);
                Template template = new Template(templateName, content);
                return Optional.of(template);
            } catch (IOException e) {
                // Fail below
            }
        }
        return Optional.absent();
    }

    /**
     * Actually sends a notification email
     *
     * @param notificationMail the order notification mail to send
     */
    private void sendNotificationMail(OrderNotificationMail notificationMail)
    {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonContext = mapper.writeValueAsString(notificationMail.getContext());
            engine.get().register(notificationMail.getTemplate());
            String html = engine.get().render(notificationMail.getTemplate().getId(), jsonContext);
            List<String> lines = IOUtils.readLines(new ByteArrayInputStream(html.getBytes()), Charsets.UTF_8);
            String subject = StringUtils.substringAfter(lines.remove(0), "Subject:").trim();
            String body = StringUtils.join(lines, "\n");
            Mail mail = new Mail().from(notificationMail.getFrom())
                    .to(notificationMail.getRecipient())
                    .text(body)
                    .subject(subject);

            emailService.sendEmail(mail);
        } catch (TemplateEngineException | IOException | MailException e) {
            logger.error("Failed to send email", ExceptionUtils.getRootCause(e));
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
            Double unitPrice = (Double) item.get("unitPrice");
            Double itemTotal = (Double) item.get("itemTotal");
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
