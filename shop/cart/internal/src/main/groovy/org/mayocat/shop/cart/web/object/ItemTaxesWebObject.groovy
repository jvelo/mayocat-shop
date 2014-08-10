package org.mayocat.shop.cart.web.object

import groovy.transform.CompileStatic

/**
 * @version $Id$
 */
@CompileStatic
class ItemTaxesWebObject
{
    ItemTaxWebObject vat

    List<ItemTaxWebObject> others
}
