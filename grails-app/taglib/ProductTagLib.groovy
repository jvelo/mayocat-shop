 class ProductTagLib {

    def price = { attrs, body ->
        def formattedPrice = String.format("%10.2f", attrs.price);
        def currency = Currency.getInstance(attrs.currency)
        def symbol = currency.getSymbol(Locale.UK) // Use UK to force euro symbol for euro instead of EUR
                                                   // FIXME need to work around this in a nicer way.
        out << """
          <span class="price">${formattedPrice} <span class="currency">${symbol}</span></span>
        """
    }
}
