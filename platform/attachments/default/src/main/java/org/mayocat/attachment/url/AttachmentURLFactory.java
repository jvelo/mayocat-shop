/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment.url;

import java.net.URL;

import javax.inject.Inject;

import org.mayocat.accounts.model.Tenant;
import org.mayocat.attachment.model.Attachment;
import org.mayocat.attachment.util.AttachmentUtils;
import org.mayocat.url.EntityURLFactory;
import org.mayocat.url.URLHelper;
import org.mayocat.url.URLType;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component
public class AttachmentURLFactory implements EntityURLFactory<Attachment>
{
    @Inject
    private URLHelper urlHelper;

    @Override
    public URL create(Attachment entity, Tenant tenant, URLType type)
    {
        String fileName = entity.getSlug() + "." + entity.getExtension();
        if (!AttachmentUtils.isImage(fileName)) {
            throw new UnsupportedOperationException("Not implemented");
        }
        return urlHelper.getTenantPlatformURL(tenant, "/images/" + fileName);
    }

    @Override
    public URL create(Attachment entity, Tenant tenant)
    {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public URL create(Attachment entity)
    {
        throw new UnsupportedOperationException("Not implemented");
    }
}
