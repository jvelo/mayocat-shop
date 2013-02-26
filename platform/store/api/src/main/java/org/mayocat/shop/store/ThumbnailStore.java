package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.model.Thumbnail;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ThumbnailStore
{
    void createOrUpdateThumbnail(Thumbnail thumbnail);

    List<Thumbnail> findAll(Attachment attachment);
}
