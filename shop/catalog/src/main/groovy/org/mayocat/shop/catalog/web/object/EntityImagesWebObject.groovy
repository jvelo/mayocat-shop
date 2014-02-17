package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude

/**
 * Doc goes here.
 *
 * @version $Id$
 */
class EntityImagesWebObject
{
    ImageWebObject featured

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ImageWebObject> all
}
