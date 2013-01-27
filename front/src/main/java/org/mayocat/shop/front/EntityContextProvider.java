package org.mayocat.shop.front;

import java.util.Map;

import org.mayocat.shop.model.Entity;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Role
public interface EntityContextProvider<E extends Entity>
{
    /**
     * The title for this entity. Used in a composition of the HTML page title.
     *
     * @param entity the entity to get the title for
     * @return an optional title for this entity
     */
    Optional<String> getTitle(E entity);

    /**
     * Get the description associated with this entity. Can be used for example to build a description meta tag.
     *
     * @param entity the entity to get the description for
     * @return an optional description for this entity
     */
    Optional<String> getDescription(E entity);

    /**
     * Get the image URL associated with this entity. Can be used for example to build a og:image meta tag.
     *
     * @param entity
     * @return an optional image URL for this entity
     */
    Optional<String> getImageURI(E entity);

    /**
     * Get the context associated with this entity. This will be the API for such an entity's page.
     *
     * @param entity the entity to get the context for
     * @return the context for this entity
     */
    Map<String, Object> getContext(E entity);
}
