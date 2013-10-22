package org.mayocat.mail;

import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Lists;

/**
 * Represents an email that can be sent from the {@link MailService}
 *
 * @version $Id$
 */
public class Mail
{
    private String from;

    private List<String> to = Lists.newArrayList();

    private List<String> cc = Lists.newArrayList();

    private List<String> bcc = Lists.newArrayList();

    private String subject;

    private String text;

    public Mail()
    {
    }

    public Mail from(String from)
    {
        this.from = from;
        return this;
    }

    public Mail to(String... to)
    {
        this.to = Arrays.asList(to);
        return this;
    }

    public Mail cc(String... cc)
    {
        this.cc = Arrays.asList(cc);
        return this;
    }

    public Mail bcc(String... bcc)
    {
        this.bcc = Arrays.asList(bcc);
        return this;
    }

    public Mail subject(String subject)
    {
        this.subject = subject;
        return this;
    }

    public Mail text(String text)
    {
        this.text = text;
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

    public String getSubject()
    {
        return subject;
    }

    public String getText()
    {
        return text;
    }
}
