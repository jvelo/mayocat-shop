package org.mayocat.shop.taxes;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public interface HasTaxes
{
    Optional<Integer> getRateIndex();
}
