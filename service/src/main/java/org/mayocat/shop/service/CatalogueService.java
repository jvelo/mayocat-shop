package org.mayocat.shop.service;

import java.util.List;

import org.mayocat.shop.model.Category;
import org.mayocat.shop.model.Product;
import org.mayocat.shop.store.EntityAlreadyExistsException;
import org.mayocat.shop.store.InvalidEntityException;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Role;

/**
 * Regroups product, product variants and catalogue operations.
 * 
 * @version $Id$
 */
@Role
public interface CatalogueService
{
    // Product operations
    
    void createProduct(Product entity) throws InvalidEntityException, EntityAlreadyExistsException, StoreException;
    
    void updateProduct(Product entity) throws InvalidEntityException, StoreException;
    
    Product findProductByHandle(String handle) throws StoreException;
    
    List<Product> findAllProducts(int number, int offset) throws StoreException;
    
    // Category operations
    
    void createCategory(Category entity) throws InvalidEntityException, EntityAlreadyExistsException, StoreException;
    
    void updateCategory(Category entity) throws InvalidEntityException, StoreException;
    
    Category findCategoryByHandle(String handle) throws StoreException;
    
    List<Category> findAllCategories(int number, int offset) throws StoreException;
}
