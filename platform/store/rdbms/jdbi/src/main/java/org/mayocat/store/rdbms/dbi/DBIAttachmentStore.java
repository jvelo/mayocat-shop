package org.mayocat.store.rdbms.dbi;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
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
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.phase.Initializable;
import org.xwiki.component.phase.InitializationException;

import com.google.common.base.Optional;
import com.google.common.base.Strings;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;

/**
 * @version $Id$
 */
@Component(hints = { "jdbi", "default" })
public class DBIAttachmentStore extends DBIEntityStore implements AttachmentStore, Initializable
{
    private static final String ATTACHMENT_TABLE_NAME = "attachment";

    private AttachmentDAO dao;

    @Inject
    private Logger logger;

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

            if (Strings.isNullOrEmpty(attachment.getExtension())) {
                // If there is no extension, try to guess it from the byte array

                Optional<String> guessedExtension = guessExtension(bytes);
                if (guessedExtension.isPresent()) {
                    attachment.setExtension(guessedExtension.get());
                }
                else {
                    throw new InvalidEntityException("No extension were set in attachment entity and we failed to " +
                            "guess one");
                }
            }

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
    public void delete(@Valid Attachment entity) throws EntityDoesNotExistException
    {
        Integer updatedRows = 0;
        this.dao.begin();
        updatedRows += this.dao.deleteEntityEntityById(ATTACHMENT_TABLE_NAME, entity.getId());
        updatedRows += this.dao.deleteEntityById(entity.getId());
        this.dao.commit();

        if (updatedRows <= 0) {
            throw new EntityDoesNotExistException("No rows was updated when trying to delete attachment");
        }
    }

    @Override
    public Integer countAll()
    {
        return this.dao.countAll(ATTACHMENT_TABLE_NAME, getTenant());
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
    public List<Attachment> findAllChildrenOf(Entity parent, List<String> extensions)
    {
        return this.dao.findAttachmentsOfEntity(parent);
    }

    @Override
    public void initialize() throws InitializationException
    {
        this.dao = this.getDbi().onDemand(AttachmentDAO.class);
        super.initialize();
    }

    private Optional<String> guessExtension(byte[] bytes)
    {
        try {
            // No extension : try to guess it
            Magic parser = new Magic();
            MagicMatch match = parser.getMagicMatch(bytes);
            String guessedExtension = match.getExtension();
            if (!Strings.isNullOrEmpty(guessedExtension)) {
                return Optional.of(guessedExtension);
            } else {
                return Optional.absent();
            }
        } catch (Exception e) {
            this.logger.warn("Error while attempting to guess attachment extension", e);
            return Optional.absent();
        }
    }


}
