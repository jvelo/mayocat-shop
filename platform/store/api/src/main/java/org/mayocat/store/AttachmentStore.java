package org.mayocat.store;

import java.util.List;
import java.util.UUID;

import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.model.Identifiable;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface AttachmentStore extends Store<Attachment, UUID>, EntityStore
{
    Attachment findBySlug(String slug);

    Attachment findBySlugAndExtension(String fileName, String extension);

    List<Attachment> findAllChildrenOf(Entity parent);

    List<Attachment> findAllChildrenOf(Entity parent, List<String> extensions);

    List<Attachment> findAllChildrenOfParentIds(List<UUID> parents);

    List<Attachment> findAllChildrenOfParentIds(List<UUID> parents, List<String> extensions);

    void detach(Attachment attachment) throws EntityDoesNotExistException;
}
