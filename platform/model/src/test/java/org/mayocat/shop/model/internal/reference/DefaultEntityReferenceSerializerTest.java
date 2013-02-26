package org.mayocat.shop.model.internal.reference;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mayocat.shop.model.reference.EntityReference;
import org.mayocat.shop.model.reference.EntityReferenceSerializer;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class DefaultEntityReferenceSerializerTest
{
    private EntityReferenceSerializer serializer;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp()
    {
        serializer = new DefaultEntityReferenceSerializer();
    }

    @Test
    public void testEntityReferenceSerializer()
    {
        EntityReference entity = new EntityReference("product", "my-product", Optional.<EntityReference>absent());
        Assert.assertEquals("product:my-product", this.serializer.serialize(entity));
    }


    @Test
    public void testSerializeEntityWithNullSlug()
    {
        EntityReference entity = new EntityReference("variant", null, Optional.<EntityReference>absent());
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid entity : type or slug is null");
        serializer.serialize(entity);
    }

    @Test
    public void testSerializeEntityWithNullType()
    {
        EntityReference entity = new EntityReference(null, "my-stuff", Optional.<EntityReference>absent());
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Invalid entity : type or slug is null");
        serializer.serialize(entity);
    }

    @Test
    public void testEntityReferenceSerializerWithParent()
    {
        EntityReference parent = new EntityReference("product", "my-product", Optional.<EntityReference>absent());
        EntityReference entity = new EntityReference("variant", "my-variant", Optional.of(parent));
        Assert.assertEquals("product:my-product:variant:my-variant", this.serializer.serialize(entity));
    }

    @Test
    public void testEntityReferenceSerializerWithTwoLevelsOfParenst()
    {
        EntityReference parent2 =
                new EntityReference("product", "my-product", Optional.<EntityReference>absent());
        EntityReference parent1 = new EntityReference("variant", "my-variant", Optional.of(parent2));
        EntityReference entity = new EntityReference("attachment", "my-attachment", Optional.of(parent1));
        Assert.assertEquals("product:my-product:variant:my-variant:attachment:my-attachment",
                this.serializer.serialize(entity));
    }
}
