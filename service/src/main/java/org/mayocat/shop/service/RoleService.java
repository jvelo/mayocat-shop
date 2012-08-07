package org.mayocat.shop.service;

import java.util.List;

import org.mayocat.shop.model.User;
import org.mayocat.shop.store.StoreException;
import org.xwiki.component.annotation.Role;

@Role
public interface RoleService extends EntityRepositoryService<org.mayocat.shop.model.Role>
{
     List<org.mayocat.shop.model.Role> findAllByUser(User user) throws StoreException;   
}
