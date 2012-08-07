package org.mayocat.shop.store;

import java.util.List;

import org.mayocat.shop.model.User;

@org.xwiki.component.annotation.Role
public interface RoleStore extends Store<org.mayocat.shop.model.Role, Long>
{
    org.mayocat.shop.model.Role findById(Long id) throws StoreException;

    List<org.mayocat.shop.model.Role> findAllByUser(User user) throws StoreException;
}
