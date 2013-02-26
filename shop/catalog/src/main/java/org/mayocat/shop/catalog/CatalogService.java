package org.mayocat.shop.catalog;

import java.util.List;

import org.mayocat.shop.catalog.model.Category;
import org.mayocat.model.EntityAndCount;
import org.mayocat.shop.catalog.model.Product;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityDoesNotExistException;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.InvalidMoveOperation;
import org.mayocat.store.InvalidOperation;
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

    Product createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException;

    void updateProduct(Product entity) throws EntityDoesNotExistException, InvalidEntityException;

    Product findProductBySlug(String slug);

    List<Product> findAllProducts(int number, int offset);

    /**
     * @return all products that does not belong to any category
     */
    List<Product> findUncategorizedProducts();

    // Category operations

    void createCategory(Category entity) throws InvalidEntityException, EntityAlreadyExistsException;

    void updateCategory(Category entity) throws EntityDoesNotExistException, InvalidEntityException;

    /**
     * @param product the product to find the categories for
     * @return all the categories this product is listed in
     */
    List<Category> findCategoriesForProduct(Product product);

    /**
     * @param category the category to find the products for
     * @return all the products this category lists
     */
    List<Product> findProductsForCategory(Category category);

    /**
     * Adds a product to a category
     *
     * @param category the slug of the category to add the product to
     * @param product the slug of the product to add
     */
    void addProductToCategory(String category, String product) throws InvalidOperation;

    /**
     * Adds a product to a category
     *
     * @param category the slug of the category to add the product to
     * @param product the slug of the product to add
     */
    void removeProductFromCategory(String category, String product) throws InvalidOperation;

    enum InsertPosition
    {
        BEFORE,
        AFTER
    }

    /**
     * Moves a product relatively to another, and shifts other products as necessary.
     *
     * @param slugOfProductToMove the slug of the product to move
     * @param slugOfProductToMoveBeforeOf the slug of the product to move before of
     * @throws org.mayocat.store.InvalidMoveOperation when no sense can be made of move parameters
     * @see {@link #moveProductInCategory(Category, String, String, InsertPosition)} Inserts the product before the
     *      relative target.
     */
    void moveProduct(String slugOfProductToMove, String slugOfProductToMoveBeforeOf)
            throws InvalidMoveOperation;

    /**
     * Moves a product relatively to another, and shifts other products as necessary.
     *
     * @param slugOfProductToMove the slug of the project to move
     * @param slugOfProductToMoveBeforeOf the slug of the product to move before of
     * @param position the relative insert position : before or after
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveProduct(String slugOfProductToMove, String slugOfProductToMoveBeforeOf, InsertPosition position)
            throws InvalidMoveOperation;

    /**
     * Moves a category relatively to another, and shifts other categories as necessary.
     *
     * @see {@link #moveProductInCategory(Category, String, String, InsertPosition)} Inserts the product before the
     *      relative target.
     * @param slugOfCategoryToMove the slug of the category to move
     * @param slugOfCategoryToMoveBeforeOf the slug of the category to move before of
     * @throws org.mayocat.store.InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveCategory(String slugOfCategoryToMove, String slugOfCategoryToMoveBeforeOf)
            throws InvalidMoveOperation;


    /**
     * Moves a category relatively to another, and shifts other categories as necessary.
     *
     * @param slugOfCategoryToMove the slug of the category to move
     * @param slugOfCategoryToMoveBeforeOf the slug of the category to move before of
     * @param position the relative insert position : before or after
     * @throws InvalidMoveOperation when no sense can be made of move parameters
     */
    void moveCategory(String slugOfCategoryToMove, String slugOfCategoryToMoveBeforeOf, InsertPosition position)
            throws InvalidMoveOperation;

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

    List<EntityAndCount<Category>> findAllCategoriesWithProductCount();
}
