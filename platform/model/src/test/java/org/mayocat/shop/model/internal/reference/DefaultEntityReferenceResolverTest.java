package org.mayocat.shop.model.internal.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.reference.EntityReference;
import org.mayocat.shop.model.reference.EntityReferenceResolver;

/**
 * @version $Id$
 */
public class DefaultEntityReferenceResolverTest
{
    private EntityReferenceResolver resolver;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp()
    {
        resolver = new DefaultEntityReferenceResolver();
    }

    @Test
    public void testEntityReferenceResolver()
    {
        EntityReference entity = resolver.resolve("product:my-product");
        Assert.assertNotNull(entity);
        Assert.assertEquals("my-product", entity.getSlug());
        Assert.assertEquals("product", entity.getType());
        Assert.assertNull(entity.getParent());
    }

    @Test
    public void testInvalidEntityReference()
    {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid entity reference : uneven number of parts");
        resolver.resolve("product:my-product:variant");
    }

    @Test
    public void testEntityReferenceResolverWithParent()
    {
        EntityReference entity = resolver.resolve("product:my-product:variant:my-variant");
        Assert.assertNotNull(entity);
        Assert.assertEquals("my-variant", entity.getSlug());
        Assert.assertEquals("variant", entity.getType());
        EntityReference parentReference = entity.getParent();
        Assert.assertEquals("my-product", parentReference.getSlug());
        Assert.assertEquals("product", parentReference.getType());
    }

    @Test
    public void testEntityReferenceResolverWithTwoLevelOfParents()
    {
        EntityReference entity = resolver.resolve("product:my-product:variant:my-variant:attachment:my-attachment");
        Assert.assertEquals("my-attachment", entity.getSlug());
        Assert.assertEquals("attachment", entity.getType());
        EntityReference parent1 = entity.getParent();
        Assert.assertNotNull(parent1);
        Assert.assertEquals("my-variant", parent1.getSlug());
        Assert.assertEquals("variant", parent1.getType());
        EntityReference parent2 = parent1.getParent();
        Assert.assertEquals("my-product", parent2.getSlug());
        Assert.assertEquals("product", parent2.getType());
    }
}
