/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail;

import javax.activation.DataSource;

/**
 * @version $Id$
 */
public class MailAttachment
{
    private final String fileName;

    private final DataSource dataSource;

    public MailAttachment(DataSource dataSource, String fileName) {
        this.fileName = fileName;
        this.dataSource = dataSource;
    }

    public String getFileName() {
        return fileName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
