package org.mayocat.authorization;

import javax.inject.Inject;
import javax.inject.Provider;

import org.mayocat.store.UserStore;
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
