package org.mayocat.store.rdbms.dbi;

import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mayocat.store.rdbms.dbi.dao.TranslationDAO;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

public class TestTranslationDAO
{

    private TranslationDAO dao;

    @Before
    public void setUp() throws Exception
    {
        DBI dbi = new DBI("jdbc:hsqldb:mem:" + UUID.randomUUID().toString());
        Handle h = dbi.open();
        h.execute("create table translation (id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, entity_id bigint, field varchar(255))");

        dao = dbi.onDemand(TranslationDAO.class);
    }

    @Test
    public void test()
    {
        Assert.assertEquals(new Long(0), dao.createTranslation(1l, "a"));
        Assert.assertEquals(new Long(1), dao.createTranslation(1l, "a"));
        Assert.assertEquals(new Long(2), dao.createTranslation(4l, "b"));
    }
}
