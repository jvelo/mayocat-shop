/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.invoicing.model;

import java.util.Date;
import java.util.UUID;

/**
 * @version $Id$
 */
public class InvoiceNumber
{
    private final UUID orderId;

    private final String number;

    private final Date generationDate;

    public InvoiceNumber(UUID orderId, String number, Date generationDate)
    {
        this.orderId = orderId;
        this.number = number;
        this.generationDate = generationDate;
    }

    public UUID getOrderId()
    {
        return orderId;
    }

    public String getNumber()
    {
        return number;
    }

    public Date getGenerationDate()
    {
        return generationDate;
    }
}

