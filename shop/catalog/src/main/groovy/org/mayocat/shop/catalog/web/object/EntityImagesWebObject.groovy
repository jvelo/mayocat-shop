package org.mayocat.shop.catalog.web.object

import com.fasterxml.jackson.annotation.JsonInclude
import groovy.transform.CompileStatic

/**
 * Web object holding an entity images. Contains a mapping for "all" images and for the "featured" image.
 *
 * @version $Id$
 */
@CompileStatic
class EntityImagesWebObject
{
    ImageWebObject featured

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<ImageWebObject> all
}
