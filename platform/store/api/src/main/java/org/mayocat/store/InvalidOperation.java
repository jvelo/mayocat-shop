/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.store;

/**
 * @version $Id$
 */
public class InvalidOperation extends Exception {
    /**
     * Generated serial UID. Change when the serialization of this class changes.
     */
    private static final long serialVersionUID = 1l;

    public InvalidOperation() {
        super();
    }

    public InvalidOperation(String message) {
        super(message);
    }
}
