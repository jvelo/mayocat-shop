package org.mayocat.shop.service.internal;

import java.text.Normalizer;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.service.CatalogService;
import org.mayocat.shop.store.InvalidMoveOperation;
import org.mayocat.shop.store.CategoryStore;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistException;
import org.mayocat.shop.store.HasOrderedCollections;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.ProductStore;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class DefaultCatalogService implements CatalogService
{
    @Inject
    private Provider<ProductStore> productStore;

    @Inject
    private Provider<CategoryStore> categoryStore;

    public void createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException
    {
        if (Strings.isNullOrEmpty(entity.getSlug())) {
            entity.setSlug(this.generateSlug(entity.getTitle()));
        }

        productStore.get().create(entity);
    }

    public void updateProduct(Product entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.productStore.get().update(entity);
    }

    public Product findProductBySlug(String slug)
    {
        return this.productStore.get().findBySlug(slug);
    }

    public List<Product> findAllProducts(int number, int offset)
    {
        return this.productStore.get().findAll(number, offset);
    }

    @Override
    public void createCategory(Category entity) throws InvalidEntityException, EntityAlreadyExistsException
    {
        if (Strings.isNullOrEmpty(entity.getSlug())) {
            entity.setSlug(this.generateSlug(entity.getTitle()));
        }
        this.categoryStore.get().create(entity);
    }

    @Override
    public void updateCategory(Category entity) throws EntityDoesNotExistException, InvalidEntityException
    {
        this.categoryStore.get().update(entity);
    }

    @Override
    public void moveCategory(String slugOfCategoryToMove, String slugOfCategoryToMoveBeforeOf)
            throws InvalidMoveOperation
    {
        this.moveCategory(slugOfCategoryToMove, slugOfCategoryToMoveBeforeOf, InsertPosition.BEFORE);
    }

    @Override
    public void moveCategory(String slugOfCategoryToMove, String slugOfCategoryToRelativeTo,
            InsertPosition position) throws InvalidMoveOperation
    {
        this.categoryStore.get().moveCategory(slugOfCategoryToMove, slugOfCategoryToRelativeTo,
                position.equals(InsertPosition.AFTER) ? HasOrderedCollections.RelativePosition.AFTER :
                        HasOrderedCollections.RelativePosition.BEFORE);
    }

    @Override
    public Category findCategoryBySlug(String slug)
    {
        return this.categoryStore.get().findBySlug(slug);
    }

    @Override
    public List<Category> findAllCategories(int number, int offset)
    {
        return this.categoryStore.get().findAll(number, offset);
    }

    private String generateSlug(String title)
    {
        return Normalizer.normalize(title.trim().toLowerCase(), java.text.Normalizer.Form.NFKD)
            .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").replaceAll("[^\\w\\ ]", "").replaceAll("\\s+", "-");
    }

    @Override
    public void moveProductInCategory(Category category, String slugOfProductToMove, String relativeSlug)
        throws InvalidMoveOperation
    {
        this.moveProductInCategory(category, slugOfProductToMove, relativeSlug, InsertPosition.BEFORE);
    }

    @Override
    public void moveProductInCategory(Category category, String slugOfProductToMove, String relativeSlug,
            InsertPosition insertPosition) throws InvalidMoveOperation
    {
        /*
        int position = -1;
        Product toMove = null;
        int i = 0;
        for (Product product : category.getProducts()) {
            if (product.getSlug().equals(slugOfProductToMove)) {
                toMove = product;
            }
        }
        if (toMove == null) {
            throw new InvalidMoveOperation();
        }

        category.getProducts().remove(toMove);

        for (Product product : category.getProducts()) {
            if (product.getSlug().equals(relativeSlug)) {
                position = i;
            }
            i++;
        }

        if (position < 0) {
            throw new InvalidMoveOperation();
        }

        switch (insertPosition) {
            case BEFORE:
                category.getProducts().add(position, toMove);
                break;
            case AFTER:
                category.getProducts().add(position + 1, toMove);
                break;
        }

        try {
            this.categoryStore.get().update(category);
        } catch (InvalidEntityException e) {
            throw new StoreException(e);
        }
        */
    }

}
