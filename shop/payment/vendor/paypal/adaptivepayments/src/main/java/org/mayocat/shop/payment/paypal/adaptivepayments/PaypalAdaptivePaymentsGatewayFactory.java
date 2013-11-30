/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.paypal.adaptivepayments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.context.WebContext;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.PaymentGateway;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.yammer.dropwizard.json.ObjectMapperFactory;

/**
 * @version $Id$
 */
@Component("paypaladaptivepayments")
public class PaypalAdaptivePaymentsGatewayFactory implements GatewayFactory
{
    public static final String ID = "paypaladaptivepayments";

    private static final String PAYMENTS_DIRECTORY = "payments";

    private static final String SLASH = "/";

    private static final String CONFIG_FILE_NAME = "config.properties";

    private static final String TENANT_CONFIGURATION_FILENAME = "configuration.yml";

    private static final String TENANTS_DIRECTORY = "tenants";

    @Inject
    private FilesSettings filesSettings;

    @Inject
    private Logger logger;

    @Inject
    private WebContext context;

    @Inject
    private ObjectMapperFactory objectMapperFactory;

    @Override
    public String getId()
    {
        return ID;
    }

    @Override
    public PaymentGateway createGateway()
    {
        File globalConfigurationFile =
                new File(filesSettings.getPermanentDirectory() + SLASH + PAYMENTS_DIRECTORY + SLASH + ID +
                        SLASH + CONFIG_FILE_NAME);

        File tenantConfigurationFile =
                new File(filesSettings.getPermanentDirectory() + SLASH + TENANTS_DIRECTORY + SLASH
                        + this.context.getTenant().getSlug() + SLASH + PAYMENTS_DIRECTORY + SLASH + ID +
                        SLASH + TENANT_CONFIGURATION_FILENAME);

        ObjectMapper mapper = objectMapperFactory.build(new YAMLFactory());

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(globalConfigurationFile);

            JsonNode node = mapper.readTree(tenantConfigurationFile);
            PaypalAdaptivePaymentsTenantConfiguration configuration =
                    mapper.readValue(new TreeTraversingParser(node), PaypalAdaptivePaymentsTenantConfiguration.class);

            return new PaypalAdaptivePaymentsPaymentGateway(inputStream, configuration.getEmail());
        } catch (FileNotFoundException e) {
            logger.error("Failed to create Paypal Adaptive payment gateway : configuration file not found");
            return null;
        } catch (JsonProcessingException e) {
            logger.error("Failed to create Paypal Adaptive payment gateway : invalid configuration file");
            return null;
        } catch (IOException e) {
            logger.error("Failed to create Paypal Adaptive payment gateway : IO exception");
            return null;
        }
    }
}
