package org.mayocat.shop.catalog.api.v1.object

import org.hibernate.validator.constraints.NotBlank

/**
 * Represents a feature API object.
 *
 * @version $Id$
 */
class FeatureApiObject
{
    final String title;

    @NotBlank
    final String slug;
}
