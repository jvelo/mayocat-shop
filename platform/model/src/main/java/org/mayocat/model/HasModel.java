/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
