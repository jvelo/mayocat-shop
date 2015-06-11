/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.webhooks;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import javax.inject.Inject;
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


    @Inject
    private ConfigurationService configurationService;

    @Inject
    private WebhooksSettings globalSettings;

    @Inject
    private Logger logger;

    public void notifyHook(final Webhook event, final Object payload) {

        Executors.newSingleThreadExecutor().submit(new Callable<Void>()
        {
            @Override
            public Void call() throws Exception
            {
                doNotifyInternal(event, payload);
                return null;
            }
        });
    }

    private void doNotifyInternal(final Webhook event, final Object payload) {
        CloseableHttpClient client = HttpClients.createDefault();
        WebhooksSettings tenantSettings = configurationService.getSettings(WebhooksSettings.class);
        List<Hook> matched = FluentIterable.from(tenantSettings.getHooks().getValue()).filter(new Predicate<Hook>()
        {
            public boolean apply(Hook hook) {
                return hook.getEvent().equals(event.getName());
            }
        }).toList();

        try {
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(payload);

            for (Hook hook : matched) {
                HttpPost request = new HttpPost(hook.getUrl());
                request.setEntity(new StringEntity(json));
                CloseableHttpResponse response = client.execute(request);
            }
        } catch (IOException e) {
            logger.error("Failed to notify hook", e);
        }
    }

}