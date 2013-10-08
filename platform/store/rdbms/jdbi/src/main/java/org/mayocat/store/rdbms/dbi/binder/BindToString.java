package org.mayocat.store.rdbms.dbi.binder;

/**
 * @version $Id$
 */

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

/**
 * @version $Id$
 */
@BindingAnnotation(BindToString.ToStringBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindToString
{
    String value();

    public static class ToStringBinderFactory implements BinderFactory
    {
        public Binder build(Annotation annotation)
        {
            return new Binder<BindToString, Object>()
            {
                public void bind(SQLStatement q, BindToString bind, Object arg)
                {
                    q.bind(bind.value(), arg.toString());
                }
            };
        }
    }
}
