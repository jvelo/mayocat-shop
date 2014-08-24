/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.cart.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.image.model.Image
import org.mayocat.rest.web.object.ImageWebObject
import org.mayocat.shop.catalog.model.Product
import org.mayocat.shop.catalog.model.Purchasable
import org.mayocat.shop.catalog.web.object.PriceWebObject
import org.mayocat.shop.taxes.PriceWithTaxes
import org.mayocat.theme.ThemeDefinition

/**
 * @version $Id$
 */
@CompileStatic
class CartItemWebObject
{
    String title

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String variant;

    String description;

    Long quantity;

    String type;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    String slug;

    UUID id;

    PriceWebObject unitPrice;

    PriceWebObject unitPriceExclusiveOfTaxes;

    PriceWebObject itemTotal;

    PriceWebObject itemTotalExclusiveOfTaxes;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    ItemTaxesWebObject taxes

    ImageWebObject featuredImage;

    def withPurchasable(Purchasable purchasable, Long quantity)
    {
        title = purchasable.title
        description = purchasable.description

        this.quantity = quantity

        if (Product.class.isAssignableFrom(purchasable.class)) {
            type = "product";
            slug = (purchasable as Product).slug
        } else {
            type = purchasable.class.simpleName.toLowerCase()
        }
    }

    def withUnitPrice(PriceWithTaxes price, Currency currency, Locale locale)
    {
        unitPrice = new PriceWebObject()
        unitPrice.withPrice(price.incl(), currency, locale)

        unitPriceExclusiveOfTaxes = new PriceWebObject()
        unitPriceExclusiveOfTaxes.withPrice(price.excl(), currency, locale)

        if (!price.vat().equals(BigDecimal.ZERO)) {
            if (taxes == null) {
                taxes = new ItemTaxesWebObject([
                        vat: new ItemTaxWebObject([
                                name: "VAT"
                        ])
                ])
            }
            taxes.vat.perUnit = new PriceWebObject()
            taxes.vat.perUnit.withPrice(price.vat(), currency, locale)
        }
    }

    def withItemTotal(PriceWithTaxes price, Currency currency, Locale locale)
    {
        itemTotal = new PriceWebObject()
        itemTotal.withPrice(price.incl(), currency, locale)

        itemTotalExclusiveOfTaxes = new PriceWebObject()
        itemTotalExclusiveOfTaxes.withPrice(price.excl(), currency, locale)

        if (!price.vat().equals(BigDecimal.ZERO)) {
            if (taxes == null) {
                taxes = new ItemTaxesWebObject([
                        vat: new ItemTaxWebObject([
                                name: "VAT"
                        ])
                ])
            }
            taxes.vat.total = new PriceWebObject()
            taxes.vat.total.withPrice(price.vat(), currency, locale)
        }
    }

    def withFeaturedImage(Image image, Optional<ThemeDefinition> themeDefinition) {
        featuredImage = new ImageWebObject()
        featuredImage.withImage(image, true, themeDefinition)
    }
}
