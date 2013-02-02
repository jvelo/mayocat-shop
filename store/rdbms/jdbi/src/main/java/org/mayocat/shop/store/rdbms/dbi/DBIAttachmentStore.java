package org.mayocat.shop.store.rdbms.dbi;

import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.mayocat.shop.model.Attachment;
import org.mayocat.shop.store.AttachmentStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.rdbms.dbi.dao.AttachmentDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIAttachmentStore extends AbstractEntityStore implements AttachmentStore, Initializable
{
    private static final String ATTACHMENT_TABLE_NAME = "attachment";

    @Inject
    private DBIProvider dbi;

    private AttachmentDAO dao;

    @Override
    public void create(Attachment attachment) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(ATTACHMENT_TABLE_NAME, attachment.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        Long entityId = this.dao.createEntity(attachment, ATTACHMENT_TABLE_NAME, getTenant());

        this.dao.createAttachment(entityId, attachment);

        this.dao.commit();
    }

    @Override public void update(@Valid Attachment entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public List<Attachment> findAll(Integer number, Integer offset)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public Attachment findById(Long id)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override public void initialize() throws InitializationException
    {
        this.dao = this.dbi.get().onDemand(AttachmentDAO.class);
    }
}
