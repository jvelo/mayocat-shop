package org.mayocat.store;

import java.util.List;

import org.mayocat.model.Attachment;
import org.mayocat.model.Thumbnail;
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
