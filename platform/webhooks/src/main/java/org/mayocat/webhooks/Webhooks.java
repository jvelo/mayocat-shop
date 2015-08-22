/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.mayocat.configuration.ConfigurationService;
import org.slf4j.Logger;
import org.xwiki.component.annotation.Component;


@Component(roles = Webhooks.class)
public class Webhooks
{
    private final static String HMAC_SHA256 = "HmacSHA256";

    @Inject
    private ConfigurationService configurationService;

    @Inject
    private Logger logger;

    public void notifyHook(final Webhook event, final Object payload) {

        WebhooksSettings hooksSettings = configurationService.getSettings(WebhooksSettings.class);
        List<Hook> hooks = FluentIterable.from(hooksSettings.getHooks().getValue()).filter(hookMatchesEvent(event)).toList();

        for (final Hook hook : hooks) {
            Executors.newSingleThreadExecutor().submit(new Callable<Void>()
            {
                @Override
                public Void call() throws Exception {
                    doNotifyHook(event, hook, payload);
                    return null;
                }
            });
        }
    }

    private Predicate<Hook> hookMatchesEvent(final Webhook event) {
        return new Predicate<Hook>()
        {
            public boolean apply(Hook hook) {
                return hook.getEvent().equals(event.getName());
            }
        };
    }

    private void doNotifyHook(final Webhook event, Hook hook, final Object payload) {
        CloseableHttpClient client = HttpClients.createDefault();

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);


            HttpPost request = new HttpPost(hook.getUrl());
            request.setEntity(new StringEntity(json));

            if (hook.getSecret().isPresent()) {
                String hmac = this.hmac(hook.getSecret().get(), json);
                request.setHeader("X-Mayocat-Signature", hmac);
            }

            request.setHeader("User-Agent", "Mayocat Webhooks/1.0");
            request.setHeader("X-Mayocat-Hook", event.getName());

            CloseableHttpResponse response = client.execute(request);

        } catch (Exception e) {
            logger.error("Failed to notify hook", e);
        }
    }

    private String hmac(String secret, String message) throws GeneralSecurityException {
        SecretKeySpec signingKey =
                new SecretKeySpec(secret.getBytes(), HMAC_SHA256);
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(signingKey);
        byte[] rawHmac = mac.doFinal(message.getBytes(Charsets.UTF_8));
        String result = new String(Hex.encodeHexString(rawHmac));
        return result;
    }

}