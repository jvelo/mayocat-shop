(function() {
    'use strict';

    MayocatShop.config(['$translateProvider', function ($translateProvider) {
        var defaultLocale = Mayocat.defaultLocale,
            localizations = Mayocat.localizations,
            localization;

        $translateProvider.useMessageFormatInterpolation();

        // Add all the localization objects to the translate provider
        for(var locale in localizations) {
            localization = localizations[locale];

            if(!localization.hasOwnProperty(locale)){
                $translateProvider.translations(locale, localization);
            }
        }

        $translateProvider.preferredLanguage(localStorage.locale || defaultLocale);
        $translateProvider.fallbackLanguage(defaultLocale);
    }]);

})();