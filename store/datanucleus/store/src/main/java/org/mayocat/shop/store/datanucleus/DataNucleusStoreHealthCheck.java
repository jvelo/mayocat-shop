package org.mayocat.shop.store.datanucleus;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.jdo.JDOHelper;

import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;
import com.yammer.metrics.core.HealthCheck;

@Component("datanucleus")
public class DataNucleusStoreHealthCheck extends HealthCheck implements org.mayocat.shop.base.HealthCheck
{

    @Inject
    private DataSourceConfiguration configuration;

    public DataNucleusStoreHealthCheck()
    {
        super("datanucleus");
    }

    @Override
    public Result check() throws Exception
    {
        if (JDOHelper.getPersistenceManagerFactory(getPersistenceProperties()).getPersistenceManager() == null) {
            return Result.unhealthy("Failed to get a persistance manager");
        }

        return Result.healthy();
    }

    private Properties getPersistenceProperties()
    {
        Properties props = new Properties();
        props.put("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.api.jdo.JDOPersistenceManagerFactory");
        props.put("datanucleus.autoCreateSchema", "true");
        props.put("datanucleus.validateTables", "false");
        props.put("datanucleus.validateConstraints", "true");
        props.put("datanucleus.identifier.case", "PreserveCase");

        // Override/extend with configuration
        for (Map.Entry<String, String> entry : this.configuration.getProperties().entrySet()) {
            props.setProperty(entry.getKey(), Strings.nullToEmpty(entry.getValue()));
        }

        return props;
    }

}
