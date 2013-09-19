(function () {
    'use strict';

    angular.module('mayocat.entitiesLocalization', [])

        .factory('entitiesLocalizationService', ['$q', 'configurationService', function ($q, configurationService) {

            var locales,
                promise;

            var getLocales = function() {

                //if we already have a promise, just return that so it doesn't run twice.
                if (promise) {
                    return promise;
                }

                var deferred = $q.defer();
                promise = deferred.promise;

                if (locales) {
                    //if we already have data, return that.
                    deferred.resolve(locales);
                } else {
                    configurationService.get("general", function (generalConfiguration) {
                        locales = {
                            main: generalConfiguration.locales.main,
                            others: generalConfiguration.locales.others
                        };
                        deferred.resolve(locales);
                    });
                }
                return promise;
            }

            return {
                getLocales: getLocales
            };

        }])

        .directive("localized", ['$rootScope', 'entitiesLocalizationService', function ($rootScope, localizationService) {
            return {
                scope: {
                },
                priority: 100, // Must execute BEFORE other directives like ck-editor, etc.
                restrict: 'A',
                transclude: 'element',
                replace: true,
                template: '' +
                    '<div class="locales-wrapper input-append"><div ng-transclude style="display:inline-block"></div>' +
                    '<span class="locales-switch add-on">' +
                    '<div class="btn-group"><a class="btn btn-mini dropdown-toggle" data-toggle="dropdown">' +
                    '<img src="/common/images/flags/{{selectedLocale}}.png"/><span class="caret"></span></a>' +
                    '<ul class="dropdown-menu">' +
                    '<li ng-repeat="locale in locales" ng-click="select(locale)"><img src="/common/images/flags/{{locale}}.png" /></li>' +
                    '</ul>' +
                    '</div>',

                compile: function (element, attrs, transclude) {
                    return {
                        post: function postLink(scope, iElement, iAttrs, controller) {
                            if (iElement.find("textarea").length > 0) {
                                iElement.find(".add-on").removeClass("add-on");
                                iElement.addClass("textarea");
                            }
                        }
                    }
                },

                controller: function ($scope, $element, $attrs) {
                    $scope.select = function(locale) {
                        $scope.selectedLocale = locale;
                    };

                    $scope.$watch('selectedLocale', function (locale, oldLocale) {
                        $rootScope.$broadcast("entity:editedLocaleChanged", {
                            "locale": $scope.selectedLocale,
                            "previously": oldLocale,
                            "isMainLocale": $scope.selectedLocale == $scope.mainLocale,
                            "wasMainLocale": $scope.mainLocale === oldLocale
                        });
                    });

                    $scope.$on("entity:editedLocaleChanged", function (event, data) {
                        if (event.currentScope !== event.targetScope) {
                            $scope.selectedLocale = data.locale
                        }
                    });

                    localizationService.getLocales().then(function (locales) {
                        $scope.mainLocale = locales.main;
                        $scope.selectedLocale = locales.main;
                        $scope.locales = [ locales.main ];
                        $scope.locales.push.apply($scope.locales, locales.others);
                    });
                }
            }
        }])
})();