package org.mayocat.shop.service;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
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
    
    void createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException, StoreException;
    
    void updateProduct(Product entity) throws InvalidEntityException, StoreException;
    
    Product findProductByHandle(String handle) throws StoreException;
    
    List<Product> findAllProducts(int number, int offset) throws StoreException;
    
    // Category operations
    
    void createCategory(Category entity) throws InvalidEntityException, EntityAlreadyExistsException, StoreException;
    
    void updateCategory(Category entity) throws InvalidEntityException, StoreException;
    
    void moveProductInCategory(Category category, String handleOfProductToMove, String handleOfProductToMoveBeforeOf) throws StoreException;
    
    Category findCategoryByHandle(String handle) throws StoreException;
    
    List<Category> findAllCategories(int number, int offset) throws StoreException;
}
