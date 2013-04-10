package org.mayocat.shop.cart.front.context;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.front.FrontContextSupplier;
import org.mayocat.shop.front.annotation.FrontContext;
import org.mayocat.shop.front.annotation.FrontContextContributor;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("cart")
public class CartContextSupplier implements FrontContextSupplier
{
    @Inject
    private CartAccessor cartAccessor;

    @FrontContextContributor(path = "/")
    public void contributeRootContext(@FrontContext Map data)
    {
        data.put("cart", new CartContext(cartAccessor.getCart(),
                // TODO we need to find a way to have Jersey @Context injection in context suppliers...
                // so that we could here for example get the request locale via @Context Locale locale
                Locale.getDefault()
        ));
    }
}
