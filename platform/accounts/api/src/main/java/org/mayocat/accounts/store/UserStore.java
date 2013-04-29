package org.mayocat.accounts.store;

import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.mayocat.accounts.model.User;
import org.mayocat.accounts.model.Role;
import org.mayocat.store.EntityAlreadyExistsException;
import org.mayocat.store.EntityStore;
import org.mayocat.store.InvalidEntityException;
import org.mayocat.store.Store;

@org.xwiki.component.annotation.Role
public interface UserStore extends Store<User, UUID>, EntityStore
{
    UUID create(@Valid User user, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException;

    User findUserByEmailOrUserName(String userNameOrEmail);

    List<Role> findRolesForUser(User user);
}
