/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment.paypal.adaptivepayments;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TreeTraversingParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import org.mayocat.shop.payment.AbstractGatewayFactory;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.PaymentGateway;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("paypaladaptivepayments")
public class PaypalAdaptivePaymentsGatewayFactory extends AbstractGatewayFactory implements GatewayFactory
{
    public static final String ID = "paypaladaptivepayments";

    private static final String CONFIG_FILE_NAME = "config.properties";

    private static final String TENANT_CONFIGURATION_FILENAME = "configuration.yml";

    @Inject
    private Logger logger;

    @Inject
    private ObjectMapper mapper;

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public PaymentGateway createGateway() {
        File globalConfigurationFile = this.getGlobalConfigurationFile(CONFIG_FILE_NAME);
        File tenantConfigurationFile = this.getTenantConfigurationFile(TENANT_CONFIGURATION_FILENAME);

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
