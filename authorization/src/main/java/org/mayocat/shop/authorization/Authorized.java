package org.mayocat.shop.authorization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to inject authenticated and authorized principal objects into protected JAX-RS resource
 * methods.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD })
public @interface Authorized
{
    boolean optional() default false;
    
    Class< ? extends Capability >[] value() default {};
}
