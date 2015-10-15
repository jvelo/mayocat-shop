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
