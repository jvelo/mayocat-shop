/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.payment;

import org.mayocat.shop.payment.model.PaymentOperation;

/**
 * Represents the response returned by {@link PaymentGateway} operations.
 *
 * @version $Id$
 */
public final class GatewayResponse
{
    /**
     * @see {@link #isSuccessful()}
     */
    private boolean isSuccessful;

    /**
     * @see {@link #getRedirectURL()}
     */
    private String redirectURL;

    /**
     * @see {@link #getOperation()}
     */
    private PaymentOperation operation;

    /**
     * @see {@link #getResponseText()}
     */
    private String responseText;

    /**
     * Constructor.
     *
     * @param isSuccessful the success status for this response. See {@link #isSuccessful()}
     * @param operation a possible operation to associate with this response. See {@link #getOperation()}
     */
    public GatewayResponse(boolean isSuccessful, PaymentOperation operation)
    {
        this.isSuccessful = isSuccessful;
        this.operation = operation;
    }

    /**
     * @return whether the payment gateway that generated this response has been successful or not. A successful gateway
     *         response does not necessarily imply that the underlying operation (for example a payment authorization)
     *         has been itself successful, it only means the execution of the operation has been successful (i.e. there
     *         were no network failure, or server failure, or any other un-expected failure).
     */
    public boolean isSuccessful()
    {
        return isSuccessful;
    }

    /**
     * @return a possible redirection URL that the gateway requests the user to be redirected to in order to continue
     *         the operation initially executed on the gateway. For example this can be a redirection to a third party
     *         web-site where the user will authorize a transfer of funds. Returns <code>null</code> when no redirection
     *         is necessary.
     */
    public String getRedirectURL()
    {
        return redirectURL;
    }

    /**
     * @param redirectURL sets the redirection URL for this response. See {@link #isRedirection()} and {@link
     * #getRedirectURL()}.
     */
    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    /**
     * @return true when this response requests that the user should be redirected to an URL. See {@link
     *         #getRedirectURL()}
     */
    public boolean isRedirection()
    {
        return redirectURL != null && !redirectURL.trim().equals("");
    }

    /**
     * @return a {@link PaymentOperation} associated with this gateway operation result, or <code>null</code> when no
     *         operation is associated.
     */
    public PaymentOperation getOperation()
    {
        return operation;
    }

    /**
     * Associates a payment operation object with this response.
     *
     * @param operation the operation to associate with this response
     */
    public void setOperation(PaymentOperation operation)
    {
        this.operation = operation;
    }

    /**
     * @return a possible response TEXT that the gateway requests the user to be redirected to in order to continue
     *         the operation initially executed on the gateway. For example this can be a redirection to a third party
     *         web-site where the user will authorize a transfer of funds. Returns <code>null</code> when no redirection
     *         is necessary.
     */
    public String getResponseText()
    {
        return responseText;
    }

    /**
     * @param responseText sets the response TEXT for this response. See {@link
     * #getResponseText()}.
     */
    public void setResponseText(String responseText)
    {
        this.responseText = responseText;
    }
}
