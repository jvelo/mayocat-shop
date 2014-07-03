/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.mayocat.addons.AddonFieldTransformer;
import org.mayocat.entity.EntityData;
import org.mayocat.image.model.Image;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

/**
 * @version $Id$
 */
@Component("image")
public class ImageAddonTransformer implements AddonFieldTransformer
{
    @Inject
    private EntityURLFactory urlFactory;

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
        final String fileName = (String) storedValue;
        List<Image> images = entityData.getDataList(Image.class);
        Optional<Image> found = FluentIterable.from(images).firstMatch(new Predicate<Image>()
        {
            public boolean apply(Image input)
            {
                return input.getAttachment().getFilename().equals(fileName);
            }
        });
        if (!found.isPresent()) {
            return Optional.absent();
        }

        Image image = found.get();

        return Optional.<Object>of(urlFactory.create(image.getAttachment()).toString());
    }
}
