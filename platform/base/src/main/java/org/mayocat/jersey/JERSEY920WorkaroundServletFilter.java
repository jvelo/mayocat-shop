/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.jersey;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * A filter to work around JERSEY-920
 *
 * https://java.net/jira/browse/JERSEY-920 https://github.com/mayocat/mayocat-shop/issues/118
 *
 * We need to remove this when using Jersey 2.0 (or something other than jersey).
 *
 * @version $Id$
 */
public class JERSEY920WorkaroundServletFilter implements Filter
{
    private Logger logger = LoggerFactory.getLogger(JERSEY920WorkaroundServletFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException
    {
        try {
            chain.doFilter(request, response);
        } catch (Throwable t) {
            // We can add here exceptions we want to log on a case by case basis.
            if (t instanceof JsonMappingException) {
                logger.error("JSON mapping exception", t);
            }
            throw t;
        }
    }

    @Override
    public void destroy()
    {
    }
}
