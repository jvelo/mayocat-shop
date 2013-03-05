package org.mayocat.shop.application;

import java.util.HashSet;
import java.util.Set;

import org.mayocat.application.AbstractService;
import org.mayocat.shop.catalog.configuration.jackson.MoneyModule;
import org.mayocat.shop.configuration.MayocatShopConfiguration;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.skife.jdbi.v2.DBI;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentRepositoryException;

import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import com.yammer.dropwizard.migrations.MigrationsBundle;

public class MayocatShopService extends AbstractService<MayocatShopConfiguration>
{
    public static final String CLIENT_RESOURCE_PATH = "/client/";

    public static void main(String[] args) throws Exception
    {
        new MayocatShopService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MayocatShopConfiguration> bootstrap)
    {
        super.initialize(bootstrap);

        bootstrap.getObjectMapperFactory().registerModule(new MoneyModule());

        bootstrap.addBundle(new AssetsBundle(CLIENT_RESOURCE_PATH, ADMIN_UI_PATH));
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new MigrationsBundle<MayocatShopConfiguration>()
        {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(MayocatShopConfiguration configuration)
            {
                return configuration.getDatabaseConfiguration();
            }
        });
    }

    private void registerDBIFactoryComponent(Environment environment, MayocatShopConfiguration configuration)
            throws ClassNotFoundException, ComponentRepositoryException
    {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "jdbi");
        final DBIProvider dbi = new DBIProvider()
        {
            @Override
            public DBI get()
            {
                return jdbi;
            }
        };
        DefaultComponentDescriptor<DBIProvider> cd = new DefaultComponentDescriptor<DBIProvider>();
        cd.setRoleType(DBIProvider.class);
        getComponentManager().registerComponent(cd, dbi);
    }

    @Override
    protected void registerComponents(MayocatShopConfiguration configuration, Environment environment)
    {
        try {
            this.registerDBIFactoryComponent(environment, configuration);
        } catch (ComponentRepositoryException e) {
            throw new RuntimeException("Failed to register DBI factory component");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to register DBI factory component");
        }
    }
}
