/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.mail;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.mayocat.views.Template;

import com.google.common.collect.Lists;

/**
 * @version $Id$
 */
public class MailTemplate
{
    private String from;

    private List<String> to;

    private List<String> cc;

    private List<String> bcc;

    private Template template;

    public MailTemplate()
    {
        to = Lists.newArrayList();
        cc = Lists.newArrayList();
        bcc = Lists.newArrayList();
    }

    public MailTemplate from(String from)
    {
        this.from = from;
        return this;
    }

    public MailTemplate to(String... to)
    {
        this.to = Arrays.asList(to);
        return this;
    }

    public MailTemplate cc(String... cc)
    {
        this.cc = Arrays.asList(cc);
        return this;
    }

    public MailTemplate bcc(String... bcc)
    {
        this.bcc = Arrays.asList(bcc);
        return this;
    }

    public MailTemplate template(Template template)
    {
        this.template = template;
        return this;
    }

    public String getFrom()
    {
        return from;
    }

    public List<String> getTo()
    {
        return to;
    }

    public List<String> getCc()
    {
        return cc;
    }

    public List<String> getBcc()
    {
        return bcc;
    }

    public Template getTemplate()
    {
        return template;
    }
}
