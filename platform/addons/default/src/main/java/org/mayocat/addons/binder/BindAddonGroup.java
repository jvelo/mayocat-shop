/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.binder;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mayocat.model.AddonGroup;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
@BindingAnnotation(BindAddonGroup.AddonGroupBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindAddonGroup
{
    String value();

    public static class AddonGroupBinderFactory implements BinderFactory
    {
        public Binder build(Annotation annotation)
        {
            return new Binder<BindAddonGroup, AddonGroup>()
            {
                public void bind(SQLStatement q, BindAddonGroup bind, AddonGroup arg)
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

                        ObjectMapper mapper = new ObjectMapper();

                        for (PropertyDescriptor prop : props) {

                            // Handle enum special cases (force a "toString + toLower" call on the enum value)
                            // See https://groups.google.com/forum/?fromgroups=#!topic/jdbi/PPqQZf7LU1k
                            if (prop.getName().equals("source")) {
                                q.bind(prefix + prop.getName(),
                                        prop.getReadMethod().invoke(arg).toString().toLowerCase());
                            } else if (prop.getName().equals("value") || prop.getName().equals("model")) {
                                // JSON serialization for value and model
                                q.bind(prefix + prop.getName(),
                                        mapper.writeValueAsString(prop.getReadMethod().invoke(arg)));
                            } else {
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
