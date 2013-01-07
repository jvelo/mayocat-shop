package org.mayocat.shop.authorization;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.shop.model.Role;
import org.mayocat.shop.model.User;
import org.mayocat.shop.store.StoreException;
import org.mayocat.shop.store.UserStore;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

@Component
public class DefaultGatekeeper implements Gatekeeper
{

    @Inject
    private Provider<UserStore> userStore;

    @Inject
    private Logger logger;

}
