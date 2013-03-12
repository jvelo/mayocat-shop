package org.mayocat.addons.binder;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mayocat.model.Addon;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

/**
 * @version $Id$
 */
@BindingAnnotation(BindAddon.AddonBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindAddon
{
    String value();

    public static class AddonBinderFactory implements BinderFactory
    {
        public Binder build(Annotation annotation)
        {
            return new Binder<BindAddon, Addon>()
            {
                public void bind(SQLStatement q, BindAddon bind, Addon arg)
                {
                    final String prefix;
                    if ("___jdbi_bare___".equals(bind.value())) {
                        prefix = "";
                    } else {
                        prefix = bind.value() + ".";
                    }

                    try {
                        BeanInfo infos = Introspector.getBeanInfo(arg.getClass());
                        PropertyDescriptor[] props = infos.getPropertyDescriptors();
                        for (PropertyDescriptor prop : props) {

                            // Handle enum special cases (force a "toString + toLower" call on the enum value)
                            // See https://groups.google.com/forum/?fromgroups=#!topic/jdbi/PPqQZf7LU1k
                            if (prop.getName().equals("source") || prop.getName().equals("type")) {
                                q.bind(prefix + prop.getName(),
                                        prop.getReadMethod().invoke(arg).toString().toLowerCase());
                            }
                            else {
                                q.bind(prefix + prop.getName(), prop.getReadMethod().invoke(arg));
                            }
                        }
                    } catch (Exception e) {
                        throw new IllegalStateException("unable to bind bean properties", e);
                    }
                }
            };
        }
    }
}
