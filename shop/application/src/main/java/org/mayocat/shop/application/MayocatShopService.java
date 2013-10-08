package org.mayocat.shop.application;

import org.mayocat.application.AbstractService;
import org.mayocat.cms.news.NewsModule;
import org.mayocat.cms.pages.PagesModule;
import org.mayocat.flyway.FlywayBundle;
import org.mayocat.shop.catalog.CatalogModule;
import org.mayocat.shop.catalog.configuration.jackson.MoneyModule;
import org.mayocat.shop.configuration.MayocatShopSettings;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.mayocat.store.rdbms.dbi.argument.PostgresUUIDArgumentFactory;
import org.skife.jdbi.v2.DBI;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentRepositoryException;

import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.db.DatabaseConfiguration;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;

public class MayocatShopService extends AbstractService<MayocatShopSettings>
{
    public static final String COMMON_PATH = "/common/";
    public static final String MANAGER_PATH = "/manager/";

    public static final String ADMIN_UI_PATH = "/admin/";

    public static final String CLIENT_RESOURCE_PATH = "/client/";

    public static void main(String[] args) throws Exception
    {
        new MayocatShopService().run(args);
    }

    @Override
    public void initialize(Bootstrap<MayocatShopSettings> bootstrap)
    {
        staticPaths.add(ADMIN_UI_PATH);
        staticPaths.add(MANAGER_PATH);
        staticPaths.add(COMMON_PATH);

        super.initialize(bootstrap);

        bootstrap.getObjectMapperFactory().registerModule(new MoneyModule());

        bootstrap.addBundle(new AssetsBundle(CLIENT_RESOURCE_PATH, ADMIN_UI_PATH));
        bootstrap.addBundle(new AssetsBundle(COMMON_PATH, COMMON_PATH));
        bootstrap.addBundle(new AssetsBundle(MANAGER_PATH, MANAGER_PATH));
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new FlywayBundle<MayocatShopSettings>()
        {
            @Override
            public DatabaseConfiguration getDatabaseConfiguration(MayocatShopSettings configuration)
            {
                return configuration.getDatabaseConfiguration();
            }
        });

        addModule(new PagesModule());
        addModule(new NewsModule());
        addModule(new CatalogModule());
    }

    private void registerDBIFactoryComponent(Environment environment, MayocatShopSettings configuration)
            throws ClassNotFoundException, ComponentRepositoryException
    {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDatabaseConfiguration(), "jdbi");
        jdbi.registerArgumentFactory(new PostgresUUIDArgumentFactory());
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
    protected void registerComponents(MayocatShopSettings configuration, Environment environment)
    {
        try {
            this.registerDBIFactoryComponent(environment, configuration);
        } catch (ComponentRepositoryException e) {
            throw new RuntimeException("Failed to register DBI factory component", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to register DBI factory component", e);
        }
    }
}
