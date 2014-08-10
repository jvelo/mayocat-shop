package org.mayocat.shop.cart.web.object

import groovy.transform.CompileStatic
import org.mayocat.shop.catalog.web.object.PriceWebObject

/**
 * @version $Id$
 */
@CompileStatic
class TaxWebObject
{
    String name

    PriceWebObject value
}
