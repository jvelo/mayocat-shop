package org.mayocat.shop.store;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import org.mayocat.shop.model.Entity;

public interface Store<T extends Entity, K extends Serializable>
{
    void create(@Valid T entity) throws EntityAlreadyExistsException, InvalidEntityException, StoreException;
    
    void update(@Valid T entity) throws InvalidEntityException, StoreException;
    
    T findById(K id) throws StoreException;
    
    List<T> findAll(Integer number, Integer offset) throws StoreException;

}
