/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.attachment.store;

import java.util.Arrays;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.attachment.store.memory.AttachmentMemoryStore;
import org.mayocat.model.Attachment;

/**
 * @version $Id$
 */
public class AttachmentMemoryStoreTest
{
    private AttachmentStore store;

    @Before
    public void setUpStore()
    {
        store = new AttachmentMemoryStore();
    }

    @Test
    public void testCreateAttachmentAndGetBySlug() throws Exception
    {
        Attachment attachment = new Attachment();
        attachment.setSlug("my-attachment");

        store.create(attachment);

        Assert.assertSame(attachment, store.findBySlug(attachment.getSlug()));
    }

    @Test
    public void testCreateAttachmentAndGetBySlugAndExtension() throws Exception
    {
        Attachment attachment = new Attachment();
        attachment.setSlug("my-attachment");
        attachment.setExtension("txt");

        store.create(attachment);

        Assert.assertSame(attachment, store.findBySlugAndExtension(attachment.getSlug(), "txt"));
    }

    @Test
    public void testFindByParentAndExtensions() throws Exception
    {
        Attachment attachment1 = new Attachment();
        attachment1.setSlug("my-attachment1");
        attachment1.setExtension("txt");

        UUID parent = store.create(attachment1).getId();

        Attachment attachment2 = new Attachment();
        attachment2.setSlug("my-attachment2");
        attachment2.setExtension("md");
        attachment2.setParentId(parent);

        Attachment attachment3 = new Attachment();
        attachment3.setSlug("my-attachment3");
        attachment3.setExtension("ogg");

        store.create(attachment2);
        store.create(attachment3);

        Assert.assertEquals(0, store.findAllChildrenOf(attachment1, Arrays.asList("txt", "docx")).size());
        Assert.assertEquals(0, store.findAllChildrenOf(attachment2, Arrays.asList("md", "docx")).size());
        Assert.assertEquals(1, store.findAllChildrenOf(attachment1, Arrays.asList("md", "docx")).size());
    }

    @Test
    public void testFindByParentIdAndExtensions() throws Exception
    {
        Attachment attachment1 = new Attachment();
        attachment1.setSlug("my-attachment1");
        attachment1.setExtension("txt");

        attachment1 = store.create(attachment1);

        Attachment attachment2 = new Attachment();
        attachment2.setSlug("my-attachment2");
        attachment2.setExtension("md");
        attachment2.setParentId(attachment1.getId());

        Attachment attachment3 = new Attachment();
        attachment3.setSlug("my-attachment3");
        attachment3.setExtension("ogg");

        attachment2 = store.create(attachment2);
        attachment3 = store.create(attachment3);

        Assert.assertEquals(0, store.findAllChildrenOfParentIds(Arrays.asList(attachment1.getId(), attachment3.getId()),
                Arrays.asList("txt", "docx")).size());
        Assert.assertEquals(0,
                store.findAllChildrenOfParentIds(Arrays.asList(attachment2.getId()), Arrays.asList("md", "docx"))
                        .size());
        Assert.assertEquals(1,
                store.findAllChildrenOfParentIds(Arrays.asList(attachment1.getId()), Arrays.asList("md", "docx"))
                        .size());
        Assert.assertEquals(1, store.findAllChildrenOfParentIds(Arrays.asList(attachment1.getId(), attachment3.getId()),
                Arrays.asList("md", "docx")).size());
    }
}
