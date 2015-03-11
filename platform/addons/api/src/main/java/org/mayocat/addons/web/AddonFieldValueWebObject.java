/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.addons.web;

/**
 * @version $Id$
 */
public class AddonFieldValueWebObject
{
    private Object raw;

    private Object display;

    public AddonFieldValueWebObject(Object raw, Object display)
    {
        this.raw = raw;
        this.display = display;
    }

    public Object getRaw()
    {
        return raw;
    }

    public Object getDisplay()
    {
        return display;
    }
}
