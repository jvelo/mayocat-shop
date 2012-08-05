package org.mayocat.shop.store;

import java.io.Serializable;

import javax.validation.Valid;

import org.mayocat.shop.model.Entity;

public interface Store<T extends Entity, K extends Serializable>
{
    void create(@Valid T entity) throws EntityAlreadyExistsException, StoreException;
    
    void update(@Valid T entity) throws StoreException;
    
    T findById(K id) throws StoreException;

}
