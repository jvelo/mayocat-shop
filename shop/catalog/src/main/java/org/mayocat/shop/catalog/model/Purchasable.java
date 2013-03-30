package org.mayocat.shop.catalog.model;

import java.math.BigDecimal;

import org.mayocat.model.Identifiable;

/**
 * @version $Id$
 */
public interface Purchasable extends Identifiable
{

    String getTitle();

    String getDescription();

    BigDecimal getUnitPrice();
}
