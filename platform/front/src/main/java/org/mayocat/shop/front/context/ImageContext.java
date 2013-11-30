/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.context;

import java.util.HashMap;

/**
 * @version $Id$
 */
public class ImageContext extends HashMap
{
    public ImageContext(String url)
    {
        this.setUrl(url);
    }

    public void setUrl(String url)
    {
        this.put("url", url);
    }

    public void setTitle(String title)
    {
        this.put("title", title);
    }

    public void setDescription(String description)
    {
        this.put("description", description);
    }
}
