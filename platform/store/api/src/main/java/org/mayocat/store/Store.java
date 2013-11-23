package org.mayocat.store;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.model.Identifiable;

public interface Store<T extends Identifiable, K extends Serializable>
{
    T create(@Valid T entity) throws EntityAlreadyExistsException, InvalidEntityException;
    
    void update(@Valid T entity) throws EntityDoesNotExistException, InvalidEntityException;

    void delete(@Valid T entity) throws EntityDoesNotExistException;

    Integer countAll();

    List<T> findAll(Integer number, Integer offset);

    List<T> findByIds(List<UUID> ids);

    T findById(UUID id);

    // boolean exists(UUID id);
}