package org.mayocat.store.rdbms.dbi;

import org.skife.jdbi.v2.DBI;
import org.xwiki.component.annotation.Component;

@Component
public interface DBIProvider
{
    DBI get();
}
