/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.shipping.store.jdbi;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.mayocat.shop.shipping.Strategy;
import org.mayocat.shop.shipping.model.Carrier;
import org.skife.jdbi.v2.SQLStatement;
import org.skife.jdbi.v2.sqlobject.Binder;
import org.skife.jdbi.v2.sqlobject.BinderFactory;
import org.skife.jdbi.v2.sqlobject.BindingAnnotation;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @version $Id$
 */
@BindingAnnotation(BindCarrier.CarrierBinderFactory.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface BindCarrier
{
    String value();

    public static class CarrierBinderFactory implements BinderFactory
    {
        @Override
        public Binder build(Annotation annotation)
        {
            return new Binder<BindCarrier, Carrier>()
            {
                public void bind(SQLStatement q, BindCarrier bind, Carrier arg)
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
                            if (prop.getName().equals("strategy")) {
                                Object value = prop.getReadMethod().invoke(arg);
                                if (value != null) {
                                    q.bind(prefix + prop.getName(), value.toString().toLowerCase());
                                } else {
                                    q.bind(prefix + prop.getName(), Strategy.NONE.toJson());
                                }
                            }
                            // Handle the destinations property that must be serialized to JSON prior write
                            else if (prop.getName().equals("destinations")) {
                                q.bind(prefix + prop.getName(), toJsonArray(prop.getReadMethod().invoke(arg)));
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

        private static String toJsonArray(Object in) throws IOException
        {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectMapper mapper = new ObjectMapper();

            mapper.writeValue(out, in);

            final byte[] data = out.toByteArray();
            return new String(data);
        }
    }
}
