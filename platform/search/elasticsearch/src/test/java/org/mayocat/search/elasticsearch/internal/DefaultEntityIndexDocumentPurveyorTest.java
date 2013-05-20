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
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.search.EntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.internal.testsupport.CustomEntity;
import org.mayocat.search.elasticsearch.internal.testsupport.CustomEntityIndexDocumentPurveyor;
import org.mayocat.search.elasticsearch.internal.testsupport.EntityWithClassLevelIndexAnnotation;
import org.mayocat.search.elasticsearch.internal.testsupport.EntityWithFeaturedImage;
import org.mayocat.search.elasticsearch.internal.testsupport.EntityWithFieldLevelIndexAnnotations;
import org.mayocat.store.AttachmentStore;
import org.mayocat.url.EntityURLFactory;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * @version $Id$
 */
@ComponentList({ EntityIndexDocumentPurveyor.class, ComponentManager.class, CustomEntityIndexDocumentPurveyor.class })
public class DefaultEntityIndexDocumentPurveyorTest
{
    private Tenant tenant;

    @Before
    public void setUp() throws ComponentLookupException, MalformedURLException
    {
        this.tenant = new Tenant();
        tenant.setSlug("tenant");

        final EntityURLFactory urlFactory = this.componentManager.getInstance(EntityURLFactory.class);
        when(urlFactory.create((Entity) anyObject(), (Tenant) anyObject()))
                .thenReturn(new URL("http://tenant.localhost:8080/api/entity/my-test-entity"));
    }

    @Rule
    public final MockitoComponentMockingRule<EntityIndexDocumentPurveyor> componentManager =
            new MockitoComponentMockingRule(DefaultEntityIndexDocumentPurveyor.class, Arrays.asList(ComponentManager.class));

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
