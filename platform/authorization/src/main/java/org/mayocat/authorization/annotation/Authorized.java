/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.authorization.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mayocat.accounts.model.Role;

/**
 * This annotation is used to inject authenticated and authorized principal objects into protected JAX-RS resource
 * methods.
 *
 * @version $Id$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Authorized
{
    /**
     * @return the list of roles allowed to access the resource protected by this annotation. A user attempting to
     * access the resource needs to have at least one of the listed roles in order to be granted access (logical OR)
     */
    Role[] roles() default {};

    /**
     * @return whether the resource guarded by this annotation requires a global (non-tenant based) user.
     */
    boolean requiresGlobalUser() default false;
}
