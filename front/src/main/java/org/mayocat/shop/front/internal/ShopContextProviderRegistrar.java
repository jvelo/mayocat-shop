package org.mayocat.shop.front.internal;

import java.util.Arrays;
import java.util.EventListenerProxy;
import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.event.ApplicationStartedEvent;
import org.mayocat.shop.front.EntityContextProviderSupplier;
import org.mayocat.shop.front.context.ProductContextProvider;
import org.mayocat.shop.model.Product;
import org.xwiki.component.annotation.Component;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

/**
 * @version $Id$
 */
@Component("shopContextProviderRegistrar")
public class ShopContextProviderRegistrar implements EventListener
{
    @Inject
    private EntityContextProviderSupplier supplier;

    @Override
    public String getName()
    {
        return "shopContextProviderRegistrar";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event> asList(new ApplicationStartedEvent());
    }

    @Override
    public void onEvent(Event event, Object o, Object o2)
    {
        supplier.registerProvider(Product.class, new ProductContextProvider());
    }
}
