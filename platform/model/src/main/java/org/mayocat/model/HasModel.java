package org.mayocat.model;

import com.google.common.base.Optional;

/**
 * Entities that implement this interface declare they support the notion of "template model".
 *
 * @version $Id$
 */
public interface HasModel
{
    /**
     * @return a present option with the name of the model that is associated with this entity, an absent option if no
     *         model is associated (usually this will lead to code fallback on the default model for that type of
     *         entity).
     */
    Optional<String> getModel();
}
