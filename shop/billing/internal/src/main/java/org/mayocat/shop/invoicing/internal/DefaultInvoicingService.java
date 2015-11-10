/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.invoicing.internal;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Maps;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Provider;
import mayoapp.dao.InvoiceNumberDao;
import org.joda.time.DateTime;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.accounts.store.TenantStore;
import org.mayocat.configuration.ConfigurationService;
import org.mayocat.configuration.general.GeneralSettings;
import org.mayocat.files.FileManager;
import org.mayocat.pdf.PdfRenderingException;
import org.mayocat.pdf.PdfTemplateRenderer;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.customer.web.object.CustomerWebObject;
import org.mayocat.shop.customer.web.object.OrderItemWebObject;
import org.mayocat.shop.customer.web.object.OrderWebObject;
import org.mayocat.shop.invoicing.InvoicingException;
import org.mayocat.shop.invoicing.InvoicingService;
import org.mayocat.shop.invoicing.InvoicingSettings;
import org.mayocat.shop.invoicing.model.InvoiceNumber;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component
public class DefaultInvoicingService implements InvoicingService, Initializable
{
    @Inject
    private PdfTemplateRenderer pdfTemplateRenderer;

    @Inject
    private FileManager fileManager;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Logger logger;

    @Inject
    private DBIProvider dbi;

    @Inject
    private Provider<TenantStore> tenantStore;

    private InvoiceNumberDao dao;

    @Override
    public void generatePdfInvoice(Order order, OutputStream outputStream) throws InvoicingException {
        Path invoicesFolderPath = fileManager.resolvePermanentFilePath(Paths.get("invoicing"));
        Map<String, Object> invoiceContext = prepareInvoiceContext(order);
        invoiceContext.put("invoiceNumber", getOrCreateInvoiceNumber(order).getNumber());
        Optional<Path> template = getTemplate();
        if (!template.isPresent()) {
            throw new InvoicingException("Cannot generate invoice : no invoice template found");
        }

        try {
            pdfTemplateRenderer.generatePDF(outputStream, template.get(), invoicesFolderPath, invoiceContext);
        } catch (PdfRenderingException e) {
            throw new InvoicingException("Failed to generate PDF invoice", e);
        }
    }

    @Override
    public synchronized InvoiceNumber getOrCreateInvoiceNumber(Order order) throws InvoicingException
    {
        this.dao.begin();

        InvoiceNumber existing = this.dao.findForOrderId(order.getId());
        if (existing != null) {
            this.dao.commit();
            return existing;
        }

        DateTime dateTime = new DateTime();
        int weekOfYear = dateTime.getWeekOfWeekyear();
        int year = dateTime.getYearOfCentury();

        String prefix = String.format("%02d%02d", year, weekOfYear);
        Integer previousCountForPrefix = this.dao.getCountForPrefix(prefix);
        String number = prefix + String.format("%03d", ++previousCountForPrefix);

        InvoiceNumber generated = new InvoiceNumber(order.getId(), number, new Date());
        this.dao.createInvoiceNumber(generated);

        this.dao.commit();
        return generated;
    }

    @Override
    public boolean isEnabledInContext() {
        InvoicingSettings settings = configurationService.getSettings(InvoicingSettings.class);
        return settings.getEnabled().getValue();
    }

    protected Map<String, Object> prepareInvoiceContext(final Order order) {
        Map<String, Object> result = Maps.newHashMap();

        Tenant tenant = tenantStore.get().findById(order.getTenantId());
        GeneralSettings settings = configurationService.getSettings(GeneralSettings.class, tenant);
        // Note: we are always using the store default locale as invoice locale
        final Locale locale = settings.getLocales().getMainLocale().getValue();

        OrderWebObject orderWebObject = new OrderWebObject().withOrder(order, locale);
        orderWebObject.setItems(
                FluentIterable.from(order.getOrderItems()).transform(new Function<OrderItem, OrderItemWebObject>()
                {
                    public OrderItemWebObject apply(OrderItem orderItem) {
                        return new OrderItemWebObject().withOrderItem(orderItem, order.getCurrency(), locale);
                    }
                }).toList());
        result.put("order", orderWebObject);

        if (order.getCustomer() != null) {
            CustomerWebObject customerWebObject = new CustomerWebObject();
            customerWebObject.withCustomer(order.getCustomer());
            if (order.getDeliveryAddress() != null) {
                customerWebObject.withDeliveryAddress(order.getDeliveryAddress());
            }
            if (order.getBillingAddress() != null) {
                customerWebObject.withBillingAddress(order.getBillingAddress());
            }

            result.put("customer", customerWebObject);
        }

        return result;
    }

    private Optional<Path> getTemplate() {
        String templateName = "invoice.twig";
        Path templateFilePath = fileManager.resolvePermanentFilePath(Paths.get("invoicing")).resolve(templateName);

        if (templateFilePath.toFile().isFile()) {
            return Optional.of(templateFilePath);
        }
        return Optional.absent();
    }

    @Override
    public void initialize() throws InitializationException {
        this.dao = dbi.get().onDemand(InvoiceNumberDao.class);
    }
}
