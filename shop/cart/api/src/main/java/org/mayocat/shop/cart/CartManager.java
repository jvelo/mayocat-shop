/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart;

import org.mayocat.shop.shipping.ShippingOption;
import org.mayocat.shop.taxes.Taxable;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface CartManager
{
    Cart getCart();

    Taxable getItem(Integer index);

    void addItem(Taxable purchasable);

    void addItem(Taxable purchasable, Long quantity);

    void removeItem(Taxable purchasable) throws InvalidCartOperationException;

    void removeItem(Integer index) throws InvalidCartOperationException;

    void setItem(Taxable purchasable, Integer index) throws InvalidCartOperationException;

    void setQuantity(Taxable purchasable, Long quantity) throws InvalidCartOperationException;

    void setQuantity(Integer index, Long quantity) throws InvalidCartOperationException;

    void setSelectedShippingOption(ShippingOption option);

    void discardCart();
}
