package org.mayocat.search.elasticsearch.internal;

import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.mayocat.model.Attachment;
import org.mayocat.model.Entity;
import org.mayocat.model.HasFeaturedImage;
import org.mayocat.model.annotation.DoNotIndex;
import org.mayocat.model.annotation.Index;
import org.mayocat.search.elasticsearch.EntitySourceExtractor;
import org.mayocat.store.AttachmentStore;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.annotation.ComponentList;
import org.xwiki.test.mockito.MockitoComponentMockingRule;

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

/**
 * @version $Id$
 */
@ComponentList({ EntitySourceExtractor.class })
public class DefaultEntitySourceExtractorTest
{
    @Index class EntityWithClassLevelIndexAnnotation implements Entity
    {
        @DoNotIndex
        private UUID id;

        private String slug;

        public EntityWithClassLevelIndexAnnotation(UUID id, String slug)
        {
            this.id = id;
            this.slug = slug;
        }

        public UUID getId()
        {
            return id;
        }

        public void setId(UUID id)
        {
            this.id = id;
        }

        public String getSlug()
        {
            return slug;
        }

        public void setSlug(String slug)
        {
            this.slug = slug;
        }
    }

    class EntityWithFieldLevelIndexAnnotations implements Entity
    {
        private UUID id;

        @Index
        private String slug;

        EntityWithFieldLevelIndexAnnotations(UUID id, String slug)
        {
            this.id = id;
            this.slug = slug;
        }

        public UUID getId()
        {
            return id;
        }

        public void setId(UUID id)
        {
            this.id = id;
        }

        public String getSlug()
        {
            return this.slug;
        }

        public void setSlug(String slug)
        {
            this.slug = slug;
        }
    }

    @Index class EntityWithFeaturedImage implements Entity, HasFeaturedImage
    {
        private UUID id;

        private UUID featuredImageId;

        private String slug;

        EntityWithFeaturedImage(UUID id, UUID featuredImageId, String slug)
        {
            this.id = id;
            this.featuredImageId = featuredImageId;
            this.slug = slug;
        }

        public UUID getFeaturedImageId()
        {
            return this.featuredImageId;
        }

        public UUID getId()
        {
            return this.id;
        }

        public void setId(UUID id)
        {
            this.id = id;
        }

        public String getSlug()
        {
            return this.slug;
        }

        public void setSlug(String slug)
        {
            this.slug = slug;
        }
    }

    @Rule
    public final MockitoComponentMockingRule<EntitySourceExtractor> componentManager =
            new MockitoComponentMockingRule(DefaultEntitySourceExtractor.class);

    @Test
    public void testExtractSourceFromEntityThatHasAClassLevelIndexAnnotation() throws ComponentLookupException
    {
        EntityWithClassLevelIndexAnnotation
                entity = new EntityWithClassLevelIndexAnnotation(UUID.randomUUID(), "my-test-entity");
        Map<String, Object> source = this.componentManager.getComponentUnderTest().extract(entity);
        Assert.assertEquals("my-test-entity", source.get("slug"));
        Assert.assertNull(source.get("id"));
    }

    @Test
    public void testExtractSourceFromEntityThatHasFieldLevelIndexAnnotation() throws ComponentLookupException
    {
        EntityWithFieldLevelIndexAnnotations
                entity = new EntityWithFieldLevelIndexAnnotations(UUID.randomUUID(), "my-test-entity");
        Map<String, Object> source = this.componentManager.getComponentUnderTest().extract(entity);
        Assert.assertEquals("my-test-entity", source.get("slug"));
        Assert.assertNull(source.get("id"));
    }

    @Test
    public void testExtractSourceWithFeaturedImage() throws ComponentLookupException
    {
        final AttachmentStore attachmentStore =
                this.componentManager.getInstance(AttachmentStore.class);

        Attachment sample = new Attachment();
        sample.setSlug("sample-attachment");
        sample.setExtension("jpg");

        when(attachmentStore.findById((UUID) anyObject())).thenReturn(sample);

        EntityWithFeaturedImage entity = new EntityWithFeaturedImage(UUID.randomUUID(), UUID.randomUUID(), "toto");

        Map<String, Object> source = this.componentManager.getComponentUnderTest().extract(entity);

        Assert.assertNotNull(source.get("featuredImage"));
        Assert.assertEquals("/image/sample-attachment.jpg", ((Map) source.get("featuredImage")).get("url"));
    }
}
