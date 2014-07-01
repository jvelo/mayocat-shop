/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import org.mayocat.addons.AddonFieldTransformer;
import org.mayocat.entity.EntityData;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
@Component("image")
public class ImageAddonTransformer implements AddonFieldTransformer
{
    public Optional<Object> fromApi(EntityData<?> entityData, Object inputValue)
    {
        return Optional.absent();
    }

    public Optional<Object> toApi(EntityData<?> entityData, Object storedValue)
    {
        return Optional.absent();
    }

    public Optional<Object> toWebView(EntityData<?> entityData, Object storedValue)
    {
        return Optional.absent();
    }
}
