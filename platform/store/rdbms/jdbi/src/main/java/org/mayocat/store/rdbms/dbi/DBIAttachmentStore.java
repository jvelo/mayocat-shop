package org.mayocat.store.rdbms.dbi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.store.AttachmentStore;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.StoreException;
import org.mayocat.store.rdbms.dbi.dao.AttachmentDAO;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIAttachmentStore extends DBIEntityStore implements AttachmentStore, Initializable
{
    private static final String ATTACHMENT_TABLE_NAME = "attachment";

    private AttachmentDAO dao;

    @Override
    public void create(Attachment attachment) throws EntityAlreadyExistsException, InvalidEntityException
    {
        if (this.dao.findBySlug(ATTACHMENT_TABLE_NAME, attachment.getSlug(), getTenant()) != null) {
            throw new EntityAlreadyExistsException();
        }

        this.dao.begin();

        Long entityId = this.dao.createChildEntity(attachment, ATTACHMENT_TABLE_NAME, getTenant());

        InputStream data = attachment.getData();
        try {
            // It's too bad we have to load the attachment data in memory. It appears Postgres's JDBC driver requires
            // to know in advance the length of the data to write (contrary to MySQL's one that can stream the data
            // to the DB's blob).
            // This memory cost is mitigated by the fact the image upload operation is (relatively) not so frequent,
            // and that platform administrator can setup max upload file size.
            byte[] bytes = IOUtils.toByteArray(data);
            this.dao.createAttachment(entityId, attachment, bytes);
        } catch (IOException e) {
            throw new StoreException(e);
        }

        this.dao.commit();
    }

    @Override
    public void update(@Valid Attachment entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        //To change body of implemented methods use File | ExposedSettings | File Templates.
    }

    @Override
    public List<Attachment> findAll(Integer number, Integer offset)
    {
        return this.dao.findAll(ATTACHMENT_TABLE_NAME, getTenant());
    }

    @Override
    public Attachment findById(Long id)
    {
        return this.dao.findById(ATTACHMENT_TABLE_NAME, id);
    }

    @Override
    public Attachment findBySlugAndExtension(String fileName, String extension)
    {
        return this.dao.findByFileNameAndExtension(fileName, extension, getTenant());
    }

    @Override
    public Attachment findBySlug(String slug)
    {
        return this.dao.findBySlug(slug, getTenant());
    }

    @Override
    public List<Attachment> findAllChildrenOf(Entity parent)
    {
        return this.dao.findAttachmentsOfEntity(parent);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(AttachmentDAO.class);
        super.initialize();
    }


}
