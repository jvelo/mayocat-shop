package org.mayocat.shop.store.datanucleus;

import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

import org.mayocat.shop.configuration.DataSourceConfiguration;
import org.xwiki.component.annotation.Component;

import com.google.common.base.Strings;

@Component
public class DefaultPersistanceManagerFactoryProdiver implements PersistanceManagerFactoryProdiver
{

    @Inject
    private DataSourceConfiguration configuration;

    private static PersistenceManagerFactory pmfInstance;

    public PersistenceManagerFactory get()
    {
        if (pmfInstance == null) {
            Properties props = extractPropertiesFromConfiguration();
            pmfInstance = JDOHelper.getPersistenceManagerFactory(props);
        }
        return pmfInstance;
    }

    private Properties extractPropertiesFromConfiguration()
    {
        Properties props = new Properties();
        for (Map.Entry<String, String> entry : configuration.getProperties().entrySet()) {
            props.put(entry.getKey(), Strings.nullToEmpty(entry.getValue()));
        }

        return props;
    }

}
