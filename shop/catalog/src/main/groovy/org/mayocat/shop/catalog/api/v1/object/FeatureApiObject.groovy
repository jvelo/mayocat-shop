package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.TypeChecked
import org.hibernate.validator.constraints.NotBlank

/**
 * Represents a feature API object.
 *
 * @version $Id$
 */
@TypeChecked
class FeatureApiObject
{
    final String title;

    @NotBlank
    final String slug;
}
