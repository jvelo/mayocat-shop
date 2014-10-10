/**
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mayocat.shop.marketplace.web.object

import com.google.common.base.Optional
import groovy.transform.CompileStatic
import org.mayocat.accounts.model.Tenant
import org.mayocat.configuration.PlatformSettings
import org.mayocat.image.model.Image
import org.mayocat.image.model.Thumbnail
import org.mayocat.image.util.ImageUtils
import org.mayocat.rest.util.RestUtils
import org.mayocat.rest.web.object.AbstractImageWebObject

import java.text.MessageFormat

/**
 * @version $Id$
 */
@CompileStatic
class MarketplaceImageWebObject extends AbstractImageWebObject
{
    def withImage(Tenant tenant, Image image, boolean isFeatured, PlatformSettings platformSettings)
    {
        put "title", RestUtils.safeString(image.attachment.title) as String;
        put "description", RestUtils.safeString(image.attachment.description) as String;
        put "featured", isFeatured
        put "url", MessageFormat.format("/images/{0}{1}.{2}",
                tenant ? (tenant.slug + "") : "",
                image.attachment.slug,
                image.attachment.extension
        );

        if (platformSettings.images && platformSettings.images.size() > 0) {
            for (String dimensionName : platformSettings.images.keySet()) {
                def definition = platformSettings.images.get(dimensionName);
                Optional<Thumbnail> bestFit = findBestFit(image, definition.width, definition.height);

                if (bestFit.isPresent()) {
                    String url = MessageFormat.format(
                            "/images/thumbnails/{0}{1}_{2,number,#}_{3,number,#}_{4,number,#}_{5,number,#}.{6}" +
                                    "?width={7,number,#}&height={8,number,#}",
                            tenant ? (tenant.slug + "/") : "",
                            image.attachment.slug,
                            bestFit.get().x,
                            bestFit.get().y,
                            bestFit.get().width,
                            bestFit.get().height,
                            image.attachment.extension,
                            definition.width,
                            definition.height
                    );
                    put "${dimensionName}_url" as String, url as String
                } else {
                    String url = MessageFormat.format("/images/{0}{1}.{2}?width={3,number,#}&height={4,number,#}",
                            tenant ? (tenant.slug + "/") : "",
                            image.attachment.slug,
                            image.attachment.extension,
                            definition.width,
                            definition.height
                    );
                    put "${dimensionName}_url" as String, url as String
                }
            }
        }
    }
}
