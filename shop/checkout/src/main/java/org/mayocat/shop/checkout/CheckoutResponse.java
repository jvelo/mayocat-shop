package org.mayocat.shop.checkout;

import com.google.common.base.Optional;

/**
 * @version $Id$
 */
public class CheckoutResponse
{
    private Optional<String> redirectURL = Optional.absent();

    public Optional<String> getRedirectURL()
    {
        return redirectURL;
    }

    public void setRedirectURL(Optional<String> redirectURL)
    {
        this.redirectURL = redirectURL;
    }
}
