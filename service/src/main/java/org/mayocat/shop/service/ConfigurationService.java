package org.mayocat.shop.service;

import org.mayocat.shop.configuration.shop.ShopConfiguration;
import org.xwiki.component.annotation.Role;

/**
 * @version $Id$
 */
@Role
public interface ConfigurationService
{
    ShopConfiguration getConfiguration();
}
