/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.front.resources;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.mayocat.model.Attachment;
import org.mayocat.shop.front.FrontContextManager;

/**
 * @version $Id$
 */
public class AbstractFrontResource
{
    protected final static Set<String> IMAGE_EXTENSIONS = new HashSet<String>();

    @Inject
    private FrontContextManager contextManager;

    static {
        IMAGE_EXTENSIONS.add("jpg");
        IMAGE_EXTENSIONS.add("jpeg");
        IMAGE_EXTENSIONS.add("png");
        IMAGE_EXTENSIONS.add("gif");
    }

    public static boolean isImage(Attachment attachment)
    {
        return IMAGE_EXTENSIONS.contains(attachment.getExtension().toLowerCase());
    }

    protected FrontContextManager getContextManager()
    {
        return contextManager;
    }

    protected Map<String, Object> getContext(UriInfo uriInfo)
    {
        return contextManager.getContext(uriInfo);
    }
}
