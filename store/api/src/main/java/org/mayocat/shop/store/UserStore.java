package org.mayocat.shop.store;

import java.util.List;

import javax.validation.Valid;

import org.mayocat.shop.model.Tenant;
import org.mayocat.shop.model.User;
import org.mayocat.shop.model.Role;

@org.xwiki.component.annotation.Role
public interface UserStore extends Store<User, Long>
{
    void create(@Valid User user, Role initialRole) throws EntityAlreadyExistsException, InvalidEntityException;

    User findUserByEmailOrUserName(String userNameOrEmail);

    List<Role> findRolesForUser(User user);
}
