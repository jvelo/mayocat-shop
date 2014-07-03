/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons;

import org.mayocat.entity.EntityData;
import org.xwiki.component.annotation.Role;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Role
public interface AddonFieldTransformer
{
    Optional<Object> fromApi(EntityData<?> entityData, Object inputValue);

    Optional<Object> toApi(EntityData<?> entityData, Object storedValue);

    Optional<Object> toWebView(EntityData<?> entityData, Object storedValue);
}
