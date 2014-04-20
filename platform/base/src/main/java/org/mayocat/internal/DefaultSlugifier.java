/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.internal;

import java.text.Normalizer;

import org.mayocat.Slugifier;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class DefaultSlugifier implements Slugifier
{
    @Override
    public String slugify(String toSlugify)
    {
        return Normalizer.normalize(toSlugify.trim().toLowerCase(), java.text.Normalizer.Form.NFKD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\w\\ ]", "")
                .trim()
                .replaceAll("\\s+", "-");
    }
}
