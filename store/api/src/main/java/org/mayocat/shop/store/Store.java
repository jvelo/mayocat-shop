package org.mayocat.shop.store;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import org.mayocat.shop.model.Entity;
import org.mayocat.shop.model.Tenant;

public interface Store<T extends Entity, K extends Serializable>
{
    void create(@Valid T entity) throws EntityAlreadyExistsException, InvalidEntityException;
    
    void update(@Valid T entity) throws InvalidEntityException;

    List<T> findAll(Integer number, Integer offset);

    T findById(K id);
}