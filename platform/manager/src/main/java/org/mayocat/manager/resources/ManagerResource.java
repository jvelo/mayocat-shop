/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.manager.resources;

import org.mayocat.rest.Resource;

/**
 * @version $Id$
 */
public interface ManagerResource extends Resource
{

    static final String MANAGER_API_ROOT_PATH = "/management" + API_ROOT_PATH;
}
