/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.catalog.internal;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.billing.event.OrderPaidEvent;
import org.mayocat.shop.billing.model.Order;
import org.mayocat.shop.billing.model.OrderItem;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.store.ProductStore;
import org.mayocat.store.EntityDoesNotExistException;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

/**
 * An event listener that respond to payment operation events.
 *
 * When a payment operation with result "CAPTURED" is received, this listener updates the status of the concerned
 * order.
 *
 * @version $Id$
 */
@Component("paidItemsStockUpdateEventListener")
public class UpdateStockWhenOrderIsPaid implements EventListener
{
    /**
     * The store to access to orders
     */
    @Inject
    private Provider<ProductStore> productStore;

    /**
     * The one with the chain-saw
     */
    @Inject
    private Logger logger;

    @Override
    public String getName()
    {
        return "paidItemsStockUpdateEventListener";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new OrderPaidEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        // Update stocks for bought products
        Order order = (Order) source;
        List<OrderItem> items = order.getOrderItems();

        for (OrderItem item : items) {
            UUID itemId = item.getPurchasableId();
            if (itemId == null) {
                return;
            }
            Long quantity = item.getQuantity();

            try {
                Product product = productStore.get().findById(itemId);
                if (product.getStock() != null) {
                    productStore.get().updateStock(itemId, -quantity.intValue());
                } else if (product.getParentId() != null) {
                    Product parent = productStore.get().findById(product.getParentId());
                    productStore.get().updateStock(product.getParentId(), -quantity.intValue());
                }
            } catch (EntityDoesNotExistException e) {
                // Ignore, there is just no stock to update
            }
        }
    }
}
