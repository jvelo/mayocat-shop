package org.mayocat.shop.service.internal;

import java.text.Normalizer;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class DefaultCatalogService implements CatalogService
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<CategoryStore> categoryStore;

    public void createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException,
        StoreException
    {
        Category allProducts = this.categoryStore.get().findByHandle("_all");
        if (allProducts == null) {
            // Lazily create the "all products" special category
            allProducts = new Category();
            allProducts.setHandle("_all");
            allProducts.setTitle("");
            allProducts.setSpecial(true);
            this.categoryStore.get().create(allProducts);
        }

        if (Strings.isNullOrEmpty(entity.getHandle())) {
            entity.setHandle(this.generateHandle(entity.getTitle()));
        }
        
        // We could just update/create the entity, but no "product created event would be fired, so
        // we save the products in base explicitly.
        productStore.get().create(entity);

        allProducts.addToProducts(entity);

        this.categoryStore.get().update(allProducts);
    }

    public void updateProduct(Product entity) throws InvalidEntityException, StoreException
    {
        this.productStore.get().update(entity);
    }

    public Product findProductByHandle(String handle) throws StoreException
    {
        return this.productStore.get().findByHandle(handle);
    }

    public List<Product> findAllProducts(int number, int offset) throws StoreException
    {
        Category category = this.categoryStore.get().findByHandle("_all");
        return category.getProducts();
    }

    @Override
    public void createCategory(Category entity) throws InvalidEntityException, EntityAlreadyExistsException,
        StoreException
    {
        if (Strings.isNullOrEmpty(entity.getHandle())) {
            entity.setHandle(this.generateHandle(entity.getTitle()));
        }
        this.categoryStore.get().create(entity);
    }

    @Override
    public void updateCategory(Category entity) throws InvalidEntityException, StoreException
    {
        this.categoryStore.get().update(entity);
    }

    @Override
    public Category findCategoryByHandle(String handle) throws StoreException
    {
        return this.categoryStore.get().findByHandle(handle);
    }

    @Override
    public List<Category> findAllCategories(int number, int offset) throws StoreException
    {
        return this.categoryStore.get().findAll(number, offset);
    }

    private String generateHandle(String title)
    {
        return Normalizer.normalize(title.trim().toLowerCase(), java.text.Normalizer.Form.NFKD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\w\\ ]", "").replaceAll("\\s+", "-");
    }

    @Override
    public void moveProductInCategory(Category category, String handleOfProductToMove, String handleOfProductToMoveBeforeOf)
        throws StoreException
    {
        int position = -1;
        Product toMove = null;
        int i = 0;
        for (Product product : category.getProducts()) {
            if (product.getHandle().equals(handleOfProductToMove)) {
                toMove = product;
            }
        }
        if (toMove == null) {
            // TODO throw exception
            return;
        }
        
        category.getProducts().remove(toMove);
        
        for (Product product : category.getProducts()) {
            if (product.getHandle().equals(handleOfProductToMoveBeforeOf)) {
                position = i;
            }
            i++;
        }
        
        if (position < 0) {
            // TODO throw exception            
            return;
        }
        
        category.getProducts().add(position, toMove);
        try {
            this.categoryStore.get().update(category);
        } catch (InvalidEntityException e) {
            throw new StoreException(e);
        }
    }

}
