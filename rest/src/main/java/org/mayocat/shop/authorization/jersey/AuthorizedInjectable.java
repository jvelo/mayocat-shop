package org.mayocat.shop.authorization.jersey;

import java.util.Arrays;
import java.util.Map;

import org.mayocat.shop.authorization.Authenticator;
import org.mayocat.shop.authorization.Capability;
import org.mayocat.shop.authorization.Gatekeeper;
import org.mayocat.shop.model.User;

import com.google.common.collect.Lists;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;

class AuthorizedInjectable extends AbstractHttpContextInjectable<User>
{
    private Map<String, Authenticator> authenticators;

    private Class< ? extends Capability>[] capabilities;

    private Gatekeeper gatekeeper;

    AuthorizedInjectable(Map<String, Authenticator> authenticators, Class< ? extends Capability>[] capabilities,
        Gatekeeper gatekeeper)
    {
        this.authenticators = authenticators;
    }

    @Override
    public User getValue(HttpContext c)
    {
        User user = null;
        for (String headerName : Lists.newArrayList("Authorization", "Cookie")) {
            final String headerValue = c.getRequest().getHeaderValue(headerName);
            for (Authenticator authenticator : this.authenticators.values()) {
                if (authenticator.respondTo(headerName, headerValue)) {
                    user = authenticator.verify(headerValue);
                    if (user != null) {
                        if (capabilities.length == 0) {
                            // No capability declared, just return authenticated user
                            return user;
                        }
                        // We have a valid user... now verify capabilities
                        boolean hasAllRequiredCapabilities = true;
                        for (Class< ? extends Capability> capability : Arrays.asList(this.capabilities)) {
                            hasAllRequiredCapabilities &= this.gatekeeper.hasCapability(user, capability);
                        }
                        if (hasAllRequiredCapabilities) {
                            return user;
                        }
                    }
                }
            }
        }
        return null;
    }

}
