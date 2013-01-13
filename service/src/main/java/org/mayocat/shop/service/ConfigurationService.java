package org.mayocat.shop.service;

import java.util.Map;

import org.mayocat.shop.configuration.shop.ShopConfiguration;
import org.xwiki.component.annotation.Role;

import com.google.common.collect.Multimap;

/**
 * @version $Id$
 */
@Role
public interface ConfigurationService
{
    Map<String, Object> getConfiguration();

    Map<String, Object> getConfiguration(String name);
}
