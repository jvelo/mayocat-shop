package org.mayocat.store;

import java.io.Serializable;
import java.util.List;

import javax.validation.Valid;

import org.mayocat.model.Identifiable;

public interface Store<T extends Identifiable, K extends Serializable>
{
    Long create(@Valid T entity) throws EntityAlreadyExistsException, InvalidEntityException;
    
    void update(@Valid T entity) throws EntityDoesNotExistException, InvalidEntityException;

    void delete(@Valid T entity) throws EntityDoesNotExistException;

    Integer countAll();

    List<T> findAll(Integer number, Integer offset);

    List<T> findByIds(List<Long> ids);

    T findById(K id);
}