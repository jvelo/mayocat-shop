package org.mayocat.shop.service;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.EntityDoesNotExistException;
import org.mayocat.shop.store.InvalidEntityException;
import org.xwiki.component.annotation.Role;

/**
 * Regroup product, product variants and catalog operations.
 * 
 * @version $Id$
 */
@Role
public interface CatalogService
{
    // Product operations

    void createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException;

    void updateProduct(Product entity) throws EntityDoesNotExistException, InvalidEntityException;

    Product findProductBySlug(String slug);

    List<Product> findAllProducts(int number, int offset);

    // Category operations

    void createCategory(Category entity) throws InvalidEntityException, EntityAlreadyExistsException;

    void updateCategory(Category entity) throws EntityDoesNotExistException, InvalidEntityException;

    enum InsertPosition
    {
        BEFORE,
        AFTER
    }

    /**
     * @see {@link #moveProductInCategory(Category, String, String, InsertPosition)} Inserts the product before the
     *      relative target.
     * @param category the category in which to move the project in
     * @param slugOfProductToMove the slug of the project to move
     * @param slugOfProductToMoveBeforeOf the slug of the product to move before of
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveProductInCategory(Category category, String slugOfProductToMove, String slugOfProductToMoveBeforeOf)
        throws InvalidMoveOperation;

    /**
     * Move a product in a category. This operation changes the position of a product within a category, and shifts
     * other products within the same category as necessary.
     * 
     * @param category the category in which to move the project in
     * @param slugOfProductToMove the slug of the project to move
     * @param relativeSlug the slug of the product to move relative to
     * @param position the relative insert position : before or after
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveProductInCategory(Category category, String slugOfProductToMove, String relativeSlug,
        InsertPosition position) throws InvalidMoveOperation;

    Category findCategoryBySlug(String slug);

    List<Category> findAllCategories(int number, int offset);
}
