package org.mayocat.shop.store.datanucleus;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.test.annotation.MockingRequirement;

public class DNCategoryStoreTest extends AbstractStoreEntityTestCase
{
    @MockingRequirement(exceptions = {PersistenceManagerProvider.class})
    private DNCategoryStore categoryStore;
    
    @Before
    public void setUpRequirements() throws ComponentLookupException, Exception
    {
        super.setUpRequirements();
    }
    
    @Test
    public void testCreateAndUpdateCategory() throws Exception
    {
        Category category = new Category();
        category.setSlug("my-category");
        category.setTitle("My category");
        
        Product p1 = new Product();
        p1.setTitle("P1");
        p1.setSlug("p1");

        Product p2 = new Product();
        p2.setTitle("P2");
        p2.setSlug("p2");
        
        category.addToProducts(p1);
        category.addToProducts(p2);
        
        this.categoryStore.create(category);
        
        category = this.categoryStore.findBySlug("my-category");
        
        Assert.assertEquals(2, category.getProducts().size());
        
        Assert.assertEquals(p1, category.getProducts().get(0));
        Assert.assertEquals(p2, category.getProducts().get(1));
        
        p1 = category.getProducts().get(0);
        p2 = category.getProducts().get(1);
    }
}

