package org.mayocat.shop.shipping;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import org.mayocat.configuration.ConfigurationService;
import org.mayocat.shop.catalog.model.Purchasable;
import org.mayocat.shop.shipping.configuration.ShippingSettings;
import org.mayocat.shop.shipping.model.Carrier;
import org.mayocat.shop.shipping.store.CarrierStore;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

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
}
