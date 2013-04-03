package org.mayocat.shop.cart.front.representation;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Currency;
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

    public CartRepresentation(Cart cart)
    {
        Map<Purchasable, Long> items = cart.getItems();
        for (Purchasable purchasable : items.keySet()) {
            CartItemRepresentation cir = new CartItemRepresentation();
            cir.setTitle(purchasable.getTitle());
            cir.setDescription(purchasable.getDescription());
            cir.setQuantity(items.get(purchasable));

            purchasable.getUnitPrice();

            CurrencyUnit currency = CurrencyUnit.of(cart.getCurrency());
            MoneyFormatter amountFormatter = new MoneyFormatterBuilder().
                    appendAmount(MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT).
                    appendCurrencySymbolLocalized().
                    toFormatter();
            String amount = amountFormatter.print(Money.of(currency, purchasable.getUnitPrice(), RoundingMode.HALF_EVEN));

            PriceRepresentation unitPrice = new PriceRepresentation();
            unitPrice.setAmount(amount);
            unitPrice.setCurrency(currency.getSymbol());

            cir.setUnitPrice(unitPrice);

            this.items.add(cir);
        }
    }

    public List<CartItemRepresentation> getItems()
    {
        return items;
    }
}
