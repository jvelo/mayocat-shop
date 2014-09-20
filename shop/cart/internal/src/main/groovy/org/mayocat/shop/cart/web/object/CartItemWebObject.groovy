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
class CartItemWebObject extends AbstractCartItemWebObject
{
    ImageWebObject featuredImage

    def withFeaturedImage(Image image, Optional<ThemeDefinition> themeDefinition) {
        featuredImage = new ImageWebObject()
        featuredImage.withImage(image, true, themeDefinition)
    }

}
