/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.configuration.ShippingSettings;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.store.CarrierStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.io.Resources;

/**
 * @version $Id$
 */
@Component
public class DefaultShippingService implements ShippingService
{
    @Inject
    private ComponentManager componentManager;

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private CarrierStore carrierStore;

    @Inject
    private Logger logger;

    static class Destination
    {
        private String name;

        private String code;

        public String getName()
        {
            return name;
        }

        public String getCode()
        {
            return code;
        }
    }

    private Map<String, Destination> destinations;

    @Override
    public boolean isShippingEnabled()
    {
        return !getActiveStrategy().equals(Strategy.NONE);
    }

    @Override
    public ShippingOption getOption(UUID carrierId, Map<Purchasable, Long> items)
    {
        Carrier carrier = carrierStore.findById(carrierId);
        return getOption(carrier, items);
    }

    @Override
    public Carrier getCarrier(UUID id)
    {
        return carrierStore.findById(id);
    }

    @Override
    public String getDestinationName(String destinationCode)
    {
        Destination destination = getDestinations().get(destinationCode);
        return destination == null ? null : destination.getName();
    }

    @Override
    public String getDestinationNames(List<String> destinationCodes)
    {
        if (destinationCodes == null) {
            // Garbage in, garbage out
            return null;
        }

        Collection<String> result = Collections2.transform(destinationCodes,
                new Function<String, String>()
                {
                    @Override
                    public String apply(final String code)
                    {
                        return getDestinationName(code);
                    }
                }
        );
        result = (Collections2.filter(result, Predicates.notNull()));
        Joiner joiner = Joiner.on(", ");
        return joiner.join(result);
    }

    @Override
    public List<ShippingOption> getOptions(Map<Purchasable, Long> items)
    {
        Strategy strategy = getActiveStrategy();
        if (strategy.equals(Strategy.NONE)) {
            return Collections.emptyList();
        }

        Set<Carrier> carriersForStrategy = carrierStore.findAll(strategy);

        List<ShippingOption> options = new ArrayList<ShippingOption>();
        for (Carrier carrier : carriersForStrategy) {
            options.add(getOption(carrier, items));
        }

        return options;
    }

    private ShippingOption getOption(Carrier carrier, Map<Purchasable, Long> items)
    {
        if (carrier == null) {
            return null;
        }
        Strategy strategy = carrier.getStrategy();
        try {
            StrategyPriceCalculator priceCalculator =
                    componentManager.getInstance(StrategyPriceCalculator.class, carrier.getStrategy().toJson());
            BigDecimal optionPrice = priceCalculator.getPrice(carrier, items);
            return new ShippingOption(carrier.getId(), carrier.getTitle(), optionPrice);
        } catch (ComponentLookupException e) {
            throw new RuntimeException("Failed to calculate price for strategy " + strategy.toJson()
                    + " : no such strategy calculator");
        }
    }

    private Strategy getActiveStrategy()
    {
        ShippingSettings settings = configurationService.getSettings(ShippingSettings.class);
        return settings.getStrategy().getValue();
    }

    private Map<String, Destination> getDestinations()
    {
        if (destinations == null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                destinations = mapper.readValue(Resources.toString(
                        Resources.getResource("org/mayocat/shop/shipping/destinations/earth_flat.json"), Charsets.UTF_8)
                        , new TypeReference<Map<String, Destination>>()
                {
                });
            } catch (IOException e) {
                this.logger.error("Failed to load destinations", e);
            }
        }
        return destinations;
    }
}
