package org.mayocat.shop.cart.front.representation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatter;
import org.joda.money.format.MoneyFormatterBuilder;
import org.mayocat.shop.cart.model.Cart;
import org.mayocat.shop.catalog.model.Purchasable;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class CartRepresentation
{
    private List<CartItemRepresentation> items = Lists.newArrayList();

    private PriceRepresentation total;

    public CartRepresentation(Cart cart, Locale locale)
    {
        Map<Purchasable, Long> items = cart.getItems();
        this.total = new PriceRepresentation(cart.getTotal(), cart.getCurrency(), locale);

        for (Purchasable purchasable : items.keySet()) {
            Long quantity = items.get(purchasable);
            CartItemRepresentation cir = new CartItemRepresentation();
            cir.setTitle(purchasable.getTitle());
            cir.setDescription(purchasable.getDescription());
            cir.setQuantity(quantity);

            purchasable.getUnitPrice();

            PriceRepresentation unitPrice =
                    new PriceRepresentation(purchasable.getUnitPrice(), cart.getCurrency(), locale);
            PriceRepresentation itemTotal =
                    new PriceRepresentation(cart.getItemTotal(purchasable), cart.getCurrency(), locale);

            cir.setUnitPrice(unitPrice);
            cir.setItemTotal(itemTotal);

            this.items.add(cir);
        }
    }

    public List<CartItemRepresentation> getItems()
    {
        return items;
    }

    public PriceRepresentation getTotal()
    {
        return total;
    }
}
