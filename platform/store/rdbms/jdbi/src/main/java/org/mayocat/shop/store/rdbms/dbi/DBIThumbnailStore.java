package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;

import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.model.Thumbnail;
import org.mayocat.shop.store.ThumbnailStore;
import org.mayocat.shop.store.rdbms.dbi.dao.ThumbnailDAO;
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
                .findThumbnail(thumbnail.getAttachmentId(), thumbnail.getSource(), thumbnail.getHint(),
                        thumbnail.getRatio());

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
    public void initialize() throws InitializationException
    {
        this.dao = dbi.get().onDemand(ThumbnailDAO.class);
    }
}
