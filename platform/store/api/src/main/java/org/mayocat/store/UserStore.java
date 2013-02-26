package org.mayocat.store;

import java.util.List;

import javax.validation.Valid;

import org.mayocat.model.User;
import org.mayocat.model.Role;

@org.xwiki.component.annotation.Role
public interface UserStore extends Store<User, Long>, EntityStore
{
    void create(@Valid User user, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException;

    User findUserByEmailOrUserName(String userNameOrEmail);

    List<Role> findRolesForUser(User user);
}
