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
        CurrencyUnit currency = CurrencyUnit.of(cart.getCurrency());
        MoneyFormatter amountFormatter = new MoneyFormatterBuilder().
                appendAmount(MoneyAmountStyle.ASCII_DECIMAL_COMMA_GROUP3_DOT).
                toFormatter();

        MoneyFormatter currencyFormatter = new MoneyFormatterBuilder().
                appendCurrencySymbolLocalized().
                toFormatter();
        String cartCurrency =
                currencyFormatter.withLocale(locale).print(
                        Money.of(currency, BigDecimal.TEN, RoundingMode.HALF_EVEN));

        String total =
                amountFormatter.withLocale(locale).print(Money.of(currency, cart.getTotal(), RoundingMode.HALF_EVEN));

        CurrencyRepresentation currencyRepresentation = new CurrencyRepresentation(cartCurrency, cartCurrency);
        this.total = new PriceRepresentation(total, currencyRepresentation);

        for (Purchasable purchasable : items.keySet()) {
            Long quantity = items.get(purchasable);
            CartItemRepresentation cir = new CartItemRepresentation();
            cir.setTitle(purchasable.getTitle());
            cir.setDescription(purchasable.getDescription());
            cir.setQuantity(quantity);

            purchasable.getUnitPrice();

            String unitAmount =
                    amountFormatter.withLocale(locale).print(
                            Money.of(currency, purchasable.getUnitPrice(), RoundingMode.HALF_EVEN));

            String itemAmount = amountFormatter.withLocale(locale).print(Money
                    .of(currency, purchasable.getUnitPrice().multiply(BigDecimal.valueOf(quantity)),
                            RoundingMode.HALF_EVEN));

            PriceRepresentation unitPrice = new PriceRepresentation(unitAmount, currencyRepresentation);
            PriceRepresentation itemTotal = new PriceRepresentation(itemAmount, currencyRepresentation);

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
