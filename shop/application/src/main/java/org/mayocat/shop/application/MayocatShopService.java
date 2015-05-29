/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.application;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.jdbi.DBIFactory;
import io.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import org.mayocat.application.AbstractService;
import org.mayocat.cms.home.HomePageModule;
import org.mayocat.cms.news.NewsModule;
import org.mayocat.cms.pages.PagesModule;
import org.mayocat.flyway.FlywayBundle;
import org.mayocat.shop.catalog.CatalogModule;
import org.mayocat.shop.catalog.configuration.jackson.MoneyModule;
import org.mayocat.shop.configuration.MayocatShopSettings;
import org.mayocat.shop.customer.CustomerModule;
import org.mayocat.store.rdbms.dbi.DBIProvider;
import org.mayocat.store.rdbms.dbi.argument.PostgresUUIDArgumentFactory;
import org.mayocat.store.rdbms.dbi.argument.PostgresUUIDArrayArgumentFactory;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.logging.PrintStreamLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.component.descriptor.DefaultComponentDescriptor;
import org.xwiki.component.manager.ComponentRepositoryException;

import static com.codahale.metrics.MetricRegistry.name;

public class MayocatShopService extends AbstractService<MayocatShopSettings>
{
    public static final String COMMON_PATH = "/common/";

    public static final String MANAGER_PATH = "/manager/";

    public static final String ADMIN_UI_PATH = "/admin/";

    public static final String CLIENT_RESOURCE_PATH = "/client/";

    private static final MetricRegistry metrics = new MetricRegistry();

    public static void main(String[] args) throws Exception
    {
        Timer timer = metrics.timer(name(MayocatShopService.class, "startUpTimer"));
        Timer.Context context = timer.time();
        new MayocatShopService().run(args);
        context.stop();

        Logger logger = LoggerFactory.getLogger(MayocatShopService.class);
        logger.info("\n\n\tMayocat Shop started in {} ms\n", (int) Math.round(timer.getSnapshot().getMin() / 1000000));
    }

    @Override
    public void initialize(Bootstrap<MayocatShopSettings> bootstrap)
    {
        staticPaths.add(ADMIN_UI_PATH);
        staticPaths.add(MANAGER_PATH);
        staticPaths.add(COMMON_PATH);

        super.initialize(bootstrap);

        bootstrap.getObjectMapper().registerModule(new MoneyModule());

        bootstrap.addBundle(new AssetsBundle(CLIENT_RESOURCE_PATH, ADMIN_UI_PATH, "index.htm", "admin"));
        bootstrap.addBundle(new AssetsBundle(COMMON_PATH, COMMON_PATH, null, "common"));
        bootstrap.addBundle(new AssetsBundle(MANAGER_PATH, MANAGER_PATH, "index.htm", "manager"));
        bootstrap.addBundle(new DBIExceptionsBundle());
        bootstrap.addBundle(new FlywayBundle<MayocatShopSettings>()
        {
            @Override public DataSourceFactory getDataSourceFactory(MayocatShopSettings mayocatShopSettings)
            {
                return mayocatShopSettings.getDataSourceFactory();
            }
        });

        addModule(new PagesModule());
        addModule(new NewsModule());
        addModule(new HomePageModule());
        addModule(new CatalogModule());
        addModule(new CustomerModule());
    }

    private void registerDBIFactoryComponent(Environment environment, MayocatShopSettings configuration)
            throws ClassNotFoundException, ComponentRepositoryException
    {
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment, configuration.getDataSourceFactory(), "jdbi");
        jdbi.registerArgumentFactory(new PostgresUUIDArgumentFactory());
        jdbi.registerArgumentFactory(new PostgresUUIDArrayArgumentFactory());

        if (configuration.getDevelopmentEnvironment().isEnabled() &&
                configuration.getDevelopmentEnvironment().isLogDatabaseRequests())
        {
            jdbi.setSQLLog(new PrintStreamLog());
        }

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
        } catch (ClassNotFoundException | ComponentRepositoryException e) {
            throw new RuntimeException("Failed to register DBI factory component", e);
        }
    }
}
