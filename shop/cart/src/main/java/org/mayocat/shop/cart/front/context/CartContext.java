package org.mayocat.shop.cart.front.context;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.front.representation.PriceRepresentation;
import org.mayocat.shop.catalog.model.Purchasable;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class CartContext
{
    private Long numberOfItems = 0l;

    private List<CartItemContext> items = Lists.newArrayList();

    private PriceRepresentation total;

    public CartContext(Cart cart, Locale locale)
    {
        Map<Purchasable, Long> items = cart.getItems();
        this.total = new PriceRepresentation(cart.getTotal(), cart.getCurrency(), locale);

        for (Purchasable purchasable : items.keySet()) {
            Long quantity = items.get(purchasable);

            CartItemContext cir = new CartItemContext();
            cir.setTitle(purchasable.getTitle());
            cir.setDescription(purchasable.getDescription());
            cir.setQuantity(quantity);

            PriceRepresentation unitPrice =
                    new PriceRepresentation(purchasable.getUnitPrice(), cart.getCurrency(), locale);
            PriceRepresentation itemTotal =
                    new PriceRepresentation(cart.getItemTotal(purchasable), cart.getCurrency(), locale);

            cir.setUnitPrice(unitPrice);
            cir.setItemTotal(itemTotal);

            numberOfItems += quantity;
            this.items.add(cir);
        }
    }

    public List<CartItemContext> getItems()
    {
        return items;
    }

    public PriceRepresentation getTotal()
    {
        return total;
    }

    public Long getNumberOfItems()
    {
        return numberOfItems;
    }
}
