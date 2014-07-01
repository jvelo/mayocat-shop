/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search.elasticsearch.internal;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.accounts.model.Tenant;
import org.mayocat.configuration.SiteSettings;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.search.EntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.internal.testsupport.CustomEntity;
import org.mayocat.search.elasticsearch.internal.testsupport.CustomEntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.internal.testsupport.EntityWithClassLevelIndexAnnotation;
import org.mayocat.search.elasticsearch.internal.testsupport.EntityWithFeaturedImage;
import org.mayocat.search.elasticsearch.internal.testsupport.EntityWithFieldLevelIndexAnnotations;
import org.mayocat.attachment.store.AttachmentStore;
import org.mayocat.url.DefaultEntityURLFactory;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.manager.ComponentRepositoryException;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * @version $Id$
 */
@ComponentList({ EntityIndexDocumentPurveyor.class, ComponentManager.class, CustomEntityIndexDocumentPurveyor.class,
        DefaultEntityURLFactory.class })
public class DefaultEntityIndexDocumentPurveyorTest
{
    private Tenant tenant;

    @Before
    public void setUp() throws Exception
    {
        this.tenant = new Tenant();
        tenant.setSlug("tenant");

        // Manually register the "site settings" component as it does not exists as an actual component
        // (it is registered dynamically when starting up mayocat applications, like all configuration components)
        DefaultComponentDescriptor cd = new DefaultComponentDescriptor();
        cd.setRoleType(SiteSettings.class);
        componentManager.registerComponent(cd, new SiteSettings());
    }

    @Rule
    public final MockitoComponentMockingRule<EntityIndexDocumentPurveyor> componentManager =
            new MockitoComponentMockingRule(DefaultEntityIndexDocumentPurveyor.class,
                    Arrays.asList(ComponentManager.class,
                            EntityURLFactory.class));

    @Test
    public void testGetDocumentFromEntityThatHasAClassLevelIndexAnnotation() throws ComponentLookupException
    {
        EntityWithClassLevelIndexAnnotation
                entity = new EntityWithClassLevelIndexAnnotation(UUID.randomUUID(), "my-test-entity");

        entity.setMyIndexedField("this is indexed");
        entity.setMyNotIndexField("this is NOT indexed");

        Map<String, Object> source = this.componentManager.getComponentUnderTest().purveyDocument(entity, tenant);
        Assert.assertEquals("this is indexed", ((Map<String, Object>) source.get("properties")).get("myIndexedField"));
        Assert.assertNull(((Map<String, Object>) source.get("properties")).get("id"));
    }

    @Test
    public void testGetDocumentFromEntityThatHasFieldLevelIndexAnnotation() throws ComponentLookupException
    {
        EntityWithFieldLevelIndexAnnotations
                entity = new EntityWithFieldLevelIndexAnnotations(UUID.randomUUID(), "my-test-entity");

        entity.setMyIndexedField("this is indexed");
        entity.setMyNotIndexField("this is NOT indexed");

        Map<String, Object> source = this.componentManager.getComponentUnderTest().purveyDocument(entity, tenant);
        Assert.assertEquals("this is indexed", ((Map<String, Object>) source.get("properties")).get("myIndexedField"));
        Assert.assertNull(((Map<String, Object>) source.get("properties")).get("id"));
    }

    @Test
    public void testGetDocumentFromEntityWithFeaturedImage() throws ComponentLookupException
    {
        final AttachmentStore attachmentStore =
                this.componentManager.getInstance(AttachmentStore.class);

        Attachment sample = new Attachment();
        sample.setSlug("sample-attachment");
        sample.setExtension("jpg");

        when(attachmentStore.findById((UUID) anyObject())).thenReturn(sample);

        EntityWithFeaturedImage entity = new EntityWithFeaturedImage(UUID.randomUUID(), UUID.randomUUID(), "toto");

        Map<String, Object> source = this.componentManager.getComponentUnderTest().purveyDocument(entity, tenant);

        Assert.assertNotNull(source.get("featuredImage"));
    }

    @Test
    public void testGetDocumentFromEntityWhereExistsACustomHandlerForThatType() throws Exception
    {
        CustomEntity entity = new CustomEntity();

        Map<String, Object> source = this.componentManager.getComponentUnderTest().purveyDocument(entity, tenant);
        Assert.assertEquals("world", source.get("hello"));
    }
}
