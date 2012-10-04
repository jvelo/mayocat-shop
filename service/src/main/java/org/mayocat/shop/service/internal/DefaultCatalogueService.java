package org.mayocat.shop.service.internal;

import java.text.Normalizer;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.service.CatalogueService;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.ProductStore;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class DefaultCatalogueService implements CatalogueService
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<CategoryStore> categoryStore;

    public void createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException,
        StoreException
    {
        if (Strings.isNullOrEmpty(entity.getHandle())) {
            entity.setHandle(this.generateHandle(entity.getTitle()));
        }
        this.productStore.get().create(entity);
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
        return this.productStore.get().findAll(number, offset);
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

}
