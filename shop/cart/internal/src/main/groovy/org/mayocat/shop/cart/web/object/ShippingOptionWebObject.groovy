/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.web.object

import com.fasterxml.jackson.annotation.JsonIgnore
import groovy.transform.CompileStatic
import org.mayocat.shop.catalog.web.object.PriceWebObject
import org.mayocat.shop.shipping.ShippingOption
import org.mayocat.shop.shipping.ShippingService
import org.mayocat.shop.shipping.model.Carrier

/**
 * @version $Id$
 */
@CompileStatic
class ShippingOptionWebObject
{
    UUID id;

    PriceWebObject price;

    String title;

    Boolean selected = false;

    String destinations;

    DeliveryTimeWebObject deliveryTime;

    def withOption(ShippingService service, ShippingOption option, Currency currency, Locale locale)
    {
        id = option.carrierId
        title = option.title

        price = new PriceWebObject()
        price.withPrice(option.price.incl(), currency, locale)

        Carrier carrier = service.getCarrier(option.carrierId);
        if (carrier != null) {
            deliveryTime = new DeliveryTimeWebObject([
                    minimumDays: carrier.minimumDays,
                    maximumDays: carrier.maximumDays
            ])

            destinations = service.getDestinationNames(carrier.destinations);
        }
    }
}
