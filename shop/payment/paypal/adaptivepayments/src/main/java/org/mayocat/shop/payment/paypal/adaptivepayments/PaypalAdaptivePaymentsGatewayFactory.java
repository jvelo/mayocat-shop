package org.mayocat.shop.payment.paypal.adaptivepayments;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.inject.Inject;

import org.mayocat.configuration.general.FilesSettings;
import org.mayocat.shop.payment.GatewayFactory;
import org.mayocat.shop.payment.PaymentGateway;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;

/**
 * @version $Id$
 */
@Component("paypaladaptivepayments")
public class PaypalAdaptivePaymentsGatewayFactory implements GatewayFactory
{
    private static final String PAYMENTS_DIRECTORY = "payments";

    private static final String ID = "paypaladaptivepayments";

    private static final String SLASH = "/";

    private static final String CONFIG_FILE_NAME = "config.properties";

    @Inject
    private FilesSettings filesSettings;

    @Inject
    private Logger logger;

    @Override
    public PaymentGateway createGateway()
    {
        File configurationFile =
                new File(filesSettings.getPermanentDirectory() + SLASH + PAYMENTS_DIRECTORY + SLASH + ID +
                        SLASH + CONFIG_FILE_NAME);
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(configurationFile);
            return new PaypalAdaptivePaymentsPaymentGateway(inputStream);
        } catch (FileNotFoundException e) {
            logger.error("Failed to create Paypal Adaptive payment gateway : configuration file not found");
            return null;
        }
    }
}
