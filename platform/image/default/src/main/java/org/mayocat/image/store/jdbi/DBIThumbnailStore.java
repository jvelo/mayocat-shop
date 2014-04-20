/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.image.store.jdbi;

import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.mayocat.image.store.ThumbnailStore;
import org.mayocat.model.Attachment;
import org.mayocat.image.model.Thumbnail;
import mayoapp.dao.ThumbnailDAO;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component(hints = { "default", "jdbi" })
public class DBIThumbnailStore implements ThumbnailStore, Initializable
{
    @Inject
    private DBIProvider dbi;

    private ThumbnailDAO dao;

    @Override
    public void createOrUpdateThumbnail(Thumbnail thumbnail)
    {
        this.dao.begin();

        Thumbnail existing = this.dao
                .findThumbnail(thumbnail.getAttachmentId(), thumbnail.getSource(), thumbnail.getHint());

        if (existing != null) {
            this.dao.updateThumbnail(thumbnail);
        } else {
            this.dao.createThumbnail(thumbnail);
        }

        this.dao.commit();
    }

    @Override
    public List<Thumbnail> findAll(Attachment attachment)
    {
        return this.dao.findThumbnails(attachment.getId());
    }

    @Override
    public List<Thumbnail> findAllForIds(List<UUID> ids)
    {
        return this.dao.findAllThumbnails(ids);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(ThumbnailDAO.class);
    }
}
