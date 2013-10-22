package org.mayocat.mail;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test for {@link Mail}
 *
 * @version $Id$
 */
public class MailTest
{

    @Test
    public void testEmailCreation()
    {
        Mail mail = new Mail()
                .from("admin@org")
                .to("user@org")
                .cc("manager@org", "boss@org")
                .bcc("friend@org")
                .subject("A propos")
                .text("Bluk");

        Assert.assertEquals("admin@org", mail.getFrom());
        Assert.assertEquals(Arrays.asList("user@org"), mail.getTo());
        Assert.assertEquals(Arrays.asList("manager@org", "boss@org"), mail.getCc());
        Assert.assertEquals(Arrays.asList("friend@org"), mail.getBcc());
        Assert.assertEquals("A propos", mail.getSubject());
        Assert.assertEquals("Bluk", mail.getText());
    }
}
