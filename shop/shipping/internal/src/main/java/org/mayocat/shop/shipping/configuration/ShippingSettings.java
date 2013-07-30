package org.mayocat.shop.shipping.configuration;

import java.math.BigDecimal;

import org.mayocat.configuration.Configurable;
import org.mayocat.configuration.ExposedSettings;
import org.mayocat.shop.shipping.Strategy;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @version $Id$
 */
public class ShippingSettings implements ExposedSettings
{
    private Configurable<Strategy> strategy = new Configurable<Strategy>(Strategy.NONE);

    public String getKey()
    {
        return "shipping";
    }

    public Configurable<Strategy> getStrategy()
    {
        return strategy;
    }
}
