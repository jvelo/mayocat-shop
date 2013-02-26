package org.mayocat.authorization.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mayocat.model.Role;

/**
 * This annotation is used to inject authenticated and authorized principal objects into protected JAX-RS resource
 * methods.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface Authorized
{
    Role[] roles() default {};
}
