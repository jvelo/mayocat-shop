package org.mayocat.shop.store;

import org.mayocat.shop.model.Attachment;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface AttachmentStore extends Store<Attachment, Long>, EntityStore
{
    Attachment findBySlug(String slug);

    Attachment findBySlugAndExtension(String fileName, String extension);
}
