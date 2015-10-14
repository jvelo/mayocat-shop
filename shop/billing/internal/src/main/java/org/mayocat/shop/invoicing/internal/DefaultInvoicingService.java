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
import mayoapp.dao.InvoiceNumberDao;
import org.joda.time.DateTime;
import org.mayocat.context.WebContext;
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
import org.mayocat.shop.invoicing.model.InvoiceNumber;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultInvoicingService implements InvoicingService
{
    @Inject
    private WebContext webContext;

    @Inject
    private PdfTemplateRenderer pdfTemplateRenderer;

    @Inject
    private FileManager fileManager;

    @Inject
    private Logger logger;

    @Inject
    private DBIProvider dbi;

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
            pdfTemplateRenderer.generatePDF(outputStream, invoicesFolderPath, template.get(), invoiceContext);
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

    protected Map<String, Object> prepareInvoiceContext(final Order order) {
        Map<String, Object> result = Maps.newHashMap();

        // TODO: use stored locale instead of context locale
        final Locale locale = webContext.getLocale();

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
        String templateName = "invoice.xhtml.twig";
        Path templateFilePath = fileManager.resolvePermanentFilePath(Paths.get("invoicing")).resolve(templateName);

        if (templateFilePath.toFile().isFile()) {
            return Optional.of(templateFilePath);
        }
        return Optional.absent();
    }
}
