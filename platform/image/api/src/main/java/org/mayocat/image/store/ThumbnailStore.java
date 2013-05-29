package org.mayocat.image.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.model.Attachment;
import org.mayocat.image.model.Thumbnail;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ThumbnailStore
{
    void createOrUpdateThumbnail(Thumbnail thumbnail);

    List<Thumbnail> findAll(Attachment attachment);

    List<Thumbnail> findAllForIds(List<UUID> ids);
}
