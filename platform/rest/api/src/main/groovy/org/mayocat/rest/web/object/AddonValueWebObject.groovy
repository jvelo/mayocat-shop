/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.rest.web.object

import groovy.transform.CompileStatic

/**
 * Web object for an {@link org.mayocat.model.AddonGroup} addon value
 *
 * @version $Id$
 */
@CompileStatic
class AddonValueWebObject
{
    Object raw;

    Object display;
}
