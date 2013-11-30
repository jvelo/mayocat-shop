/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function(Mayocat) {
    'use strict';

    MayocatShop.config(['$translateProvider', function ($translateProvider) {

        Mayocat.defaultLocale = Mayocat.defaultLocale || "en";

        var defaultLocale = Mayocat.defaultLocale,
            localization = Mayocat.localization,
            locale;

        $translateProvider.useMessageFormatInterpolation();

        // Add all the localization objects to the translate provider
        for(var key in localization) {
            locale = localization[key];

            if(!locale.hasOwnProperty(key)){
                $translateProvider.translations(key, locale);
            }
        }

        $translateProvider.preferredLanguage(localStorage.locale || defaultLocale);
        $translateProvider.fallbackLanguage(defaultLocale);
    }]);

})(Mayocat);