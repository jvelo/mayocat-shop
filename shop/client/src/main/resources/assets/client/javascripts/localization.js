(function() {
    'use strict';

    MayocatShop.config(['$translateProvider', function ($translateProvider) {
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

})();