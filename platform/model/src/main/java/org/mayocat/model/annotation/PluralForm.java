package org.mayocat.model.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to explicitly specify the plural form of an entity. When not used, the plural form is being
 * calculated programmatically when needed (for example URL creation).
 *
 * @version $Id$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE})
public @interface PluralForm
{
    String value();
}
