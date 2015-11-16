/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.invoicing;

import java.io.OutputStream;

import org.mayocat.shop.billing.model.Order;
import org.xwiki.component.annotation.Role;

import org.mayocat.shop.invoicing.model.InvoiceNumber;

/**
 * PDF invoicing service. Generates PDF invoices from orders.
 *
 * @version $Id$
 */
@Role
public interface InvoicingService
{
    /**
     * Writes the PDF for the passed order to the passed output stream.
     *
     * @param order the order to generate the PDF invoice for
     * @param outputStream the output stream to write the PDF to
     */
    void generatePdfInvoice(Order order, OutputStream outputStream) throws InvoicingException;

    /**
     * Creates the invoice number for the passed order
     *
     * @throws InvoicingException
     */
    InvoiceNumber getOrCreateInvoiceNumber(Order order) throws InvoicingException;

    /**
     * @return true if invoicing is enabled in current context (i.e. for the tenant the context
     * is executing under or for the global context), false otherwise.
     */
    boolean isEnabledInContext();
}
