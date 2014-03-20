package org.mayocat.rest.api.object

import groovy.transform.CompileStatic

import javax.validation.constraints.NotNull

/**
 * The API object for a {@link org.mayocat.image.model.Thumbnail} of an image.
 *
 * @version $Id$
 */
@CompileStatic
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
