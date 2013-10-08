package org.mayocat.store.rdbms.dbi.binder;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mayocat.store.rdbms.dbi.argument.JsonArgument;
import org.mayocat.store.rdbms.dbi.argument.JsonArgumentAsJsonArgumentFactory;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

/**
 * @version $Id$
 */
@BindingAnnotation(BindJson.JsonBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindJson
{
    String value();

    public static class JsonBinderFactory implements BinderFactory
    {
        public Binder build(Annotation annotation)
        {
            return new Binder<BindJson, Object>()
            {
                public void bind(SQLStatement q, BindJson bind, Object arg)
                {
                    q.registerArgumentFactory(new JsonArgumentAsJsonArgumentFactory());
                    q.bind(bind.value(), new JsonArgument(arg));
                }
            };
        }
    }
}
