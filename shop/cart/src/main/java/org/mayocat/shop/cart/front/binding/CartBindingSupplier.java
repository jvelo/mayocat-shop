package org.mayocat.shop.cart.front.binding;

import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import org.mayocat.shop.cart.CartAccessor;
import org.mayocat.shop.cart.front.representation.CartRepresentation;
import org.mayocat.shop.front.FrontBindingSupplier;
import org.mayocat.shop.front.annotation.Bindings;
import org.mayocat.shop.front.annotation.FrontBinding;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("cart")
public class CartBindingSupplier  implements FrontBindingSupplier
{
    @Inject
    private CartAccessor cartAccessor;

    @FrontBinding(path = "/")
    public void contributeRootBindings(@Bindings Map data)
    {
        data.put("cart", new CartRepresentation(cartAccessor.getCart(),
                // TODO we need to find a way to have Jersey @Context injection in binding suppliers...
                // so that we could here for example get the request locale via @Context Locale locale
                Locale.getDefault()
        ));
    }
}
