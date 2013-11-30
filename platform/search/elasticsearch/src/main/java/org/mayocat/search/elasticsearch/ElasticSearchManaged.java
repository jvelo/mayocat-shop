/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.search.elasticsearch;

import javax.inject.Inject;
import javax.inject.Named;

import org.mayocat.lifecycle.Managed;
import org.mayocat.search.SearchEngine;
import org.xwiki.component.annotation.Component;

@Component("elasticsearch")
public class ElasticSearchManaged implements Managed
{

    @Inject
    @Named("elasticsearch")
    private SearchEngine searchEngine;

    @Override
    public void start() throws Exception
    {
        ((ElasticSearchSearchEngine) searchEngine).start();
    }

    @Override
    public void stop() throws Exception
    {
        ((ElasticSearchSearchEngine) searchEngine).stop();
    }

}
