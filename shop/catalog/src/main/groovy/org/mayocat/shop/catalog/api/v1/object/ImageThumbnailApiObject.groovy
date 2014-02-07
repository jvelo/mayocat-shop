package org.mayocat.shop.catalog.api.v1.object

import groovy.transform.TypeChecked

import javax.validation.constraints.NotNull

/**
 * The API object for a {@link org.mayocat.image.model.Thumbnail} of an image.
 *
 * @version $Id$
 */
@TypeChecked
class ImageThumbnailApiObject
{
    @NotNull
    String source;

    @NotNull
    String hint;

    String ratio;

    @NotNull
    Integer x;

    @NotNull
    Integer y;

    @NotNull
    Integer width;

    @NotNull
    Integer height;
}
