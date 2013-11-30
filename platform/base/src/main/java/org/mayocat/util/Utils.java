/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.util;

import java.lang.reflect.Type;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;

/**
 * @version $Id$
 */
public class Utils
{
    /**
     * The component manager used by {@link #getComponent(Type)} and {@link #getComponent(Type, String)}. It is useful
     * for any non component code that need to initialize/access components.
     */
    private static ComponentManager componentManager;

    /**
     * @param componentManager the component manager used by {@link #getComponent(Type)} and {@link #getComponent(Type,
     * String)}
     */
    public static void setComponentManager(ComponentManager componentManager)
    {
        Utils.componentManager = componentManager;
    }

    /**
     * @return the component manager used by {@link #getComponent(Type)} and {@link #getComponent(Type, String)}
     * @deprecated starting with 4.1M2 use the Component Script Service instead
     */
    public static ComponentManager getComponentManager()
    {
        return componentManager;
    }

    /**
     * Lookup a component by role and hint. ·
     *
     * @param roleType the class (aka role) that the component implements
     * @param roleHint a value to differentiate different component implementations for the same role
     * @return the component's instance
     * @throws RuntimeException if the component cannot be found/initialized, or if the component manager is not
     * initialized
     */
    public static <T> T getComponent(Type roleType, String roleHint)
    {
        T component;

        if (componentManager != null) {
            try {
                component = componentManager.getInstance(roleType, roleHint);
            } catch (ComponentLookupException e) {
                throw new RuntimeException("Failed to load component for type [" + roleType + "] for hint [" + roleHint
                        + "]", e);
            }
        } else {
            throw new RuntimeException("Component manager has not been initialized before lookup for [" + roleType
                    + "] for hint [" + roleHint + "]");
        }

        return component;
    }

    /**
     * Lookup a component by role (uses the default hint). ·
     *
     * @param roleType the class (aka role) that the component implements
     * @return the component's instance
     * @throws RuntimeException if the component cannot be found/initialized, or if the component manager is not
     * initialized
     */
    public static <T> T getComponent(Type roleType)
    {
        return getComponent(roleType, "default");
    }

    /**
     * Lookup a XWiki component by role and hint.
     *
     * @param role the class (aka role) that the component implements
     * @param hint a value to differentiate different component implementations for the same role
     * @return the component's instance
     * @throws RuntimeException if the component cannot be found/initialized, or if the component manager is not
     * initialized
     */
    public static <T> T getComponent(Class<T> role, String hint)
    {
        return getComponent((Type) role, hint);
    }

    /**
     * Lookup a XWiki component by role (uses the default hint).
     *
     * @param role the class (aka role) that the component implements
     * @return the component's instance
     * @throws RuntimeException if the component cannot be found/initialized, or if the component manager is not
     * initialized
     */
    public static <T> T getComponent(Class<T> role)
    {
        return getComponent((Type) role);
    }
}
