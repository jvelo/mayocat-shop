(function () {

    'use strict'

    var capitalize = function (string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    angular.module('mayocat.entities', [])

        .factory('entityMixins', [
            'entityBaseMixin',
            'entityModelMixin',
            'entityAddonsMixin',
            'entityLocalizationMixin',
            'entityImageMixin',
            function () {
                var allMixins = [
                        'entityBaseMixin',
                        'entityModelMixin',
                        'entityAddonsMixin',
                        'entityLocalizationMixin',
                        'entityImageMixin'
                    ],
                    args = arguments;
                return {
                    extendAll: function ($scope, entityType, options) {
                        // Make sure the options hash exists.
                        options = typeof options === "undefined" ? {} : options;

                        // Iterate over all mixins, find its option object in the global option hash, and then extend
                        // the passed scope with it.
                        for (var i = 0; i < allMixins.length; i++) {
                            var mixin = allMixins[i];
                            var mixinName = mixin.substring(6);
                            mixinName = mixinName.substring(0, mixinName.indexOf('Mixin')).toLowerCase();
                            var mixinOptions = options[mixinName];
                            angular.extend($scope, args[i](entityType, mixinOptions));
                        }
                    }
                }
            }
        ])

        .factory('entityBaseMixin', ["$routeParams" , "$rootScope", function ($routeParams, $rootScope) {
            return function (entityType, options) {
                console.log("Options", options);
                options = typeof options === "undefined" ? {} : options;
                var mixin = {
                    slug: $routeParams[entityType],
                    isNew: function () {
                        var scope = this;
                        return scope.slug == "_new";
                    },
                    initializeEntity: function() {
                        var scope = this;

                        scope.initializeAddons && scope.initializeAddons();
                        scope.initializeModels && scope.initializeModels();
                        scope.initializeLocalization && scope.initializeLocalization();

                        $rootScope.$broadcast("entity:initialized", {
                            type: entityType,
                            uri: (options.apiBase || "/api/" + entityType + "s/") + scope.slug + "/"
                        });
                    }
                };

                // Expose a function to create a new entity instance of this entity type.
                // Example, for the entity type "page", this will expose a function newPage() that can be used
                // in the page entity partial HTML file.
                mixin["new" + capitalize(entityType)] = function () {
                    return {
                        slug: ""
                    };
                };

                return mixin;
            }
        }])

        .factory('entityModelMixin', ["configurationService", function (configurationService) {
            return function (entityType) {
                var mixin = {};

                mixin.initializeModels = function () {
                    var scope = this;
                    scope.models = [];
                    configurationService.get("entities", function (entities) {
                        if (typeof entities[entityType] !== 'undefined') {
                            for (var modelId in entities[entityType].models) {
                                if (entities[entityType].models.hasOwnProperty(modelId)) {
                                    var model = entities[entityType].models[modelId];
                                    scope.models.push({
                                        id:modelId,
                                        name:model.name
                                    });
                                }
                            }
                        }
                    });
                }
                return mixin;
            }
        }])

        .factory('entityAddonsMixin', ["addonsService", function (addonsService) {
            return function (entityType) {
                var mixin = {};

                mixin.initializeAddons = function () {
                    var scope = this;
                    scope.addons = [];
                    addonsService.initializeEntityAddons(entityType, scope[entityType]).then(function (addons) {
                        scope.addons = addons;
                    });
                }

                return mixin;
            }
        }])

        .factory('entityLocalizationMixin', [function () {

            return function (entityType, options) {
                var mixin = {},
                    localizedKey = "localized" + capitalize(entityType);

                // Will hold the localized version of this entity, that is intended to be used with ng-model in
                // entities partials. Example, for an "event" entity, the localized key will be localizedEvent,
                // and could be used in the following way it's edition partial :
                // <textarea ng-model="localizedEvent.description" ck-editor localized />
                mixin[localizedKey] = {};

                mixin.initializeLocalization = function () {
                    var scope = this;
                    scope[localizedKey] = scope[entityType];
                    scope.$on("entity:editedLocaleChanged", function (event, data) {
                        // Save edited version if necessary

                        if (typeof scope[entityType] === "undefined" || !scope[entityType].$resolved) {
                            // We are not ready
                            return;
                        }

                        if (typeof scope[entityType].localizedVersions === "undefined") {
                            scope[entityType].localizedVersions = {};
                        }

                        if (typeof scope[entityType].localizedVersions[data.locale] !== 'undefined' && !data.isMainLocale) {
                            // If there is a localized version with the new locale to be edited, then use it
                            scope[localizedKey] = scope[entityType].localizedVersions[data.locale];

                        }
                        else if (!data.isMainLocale) {
                            // Else if it's not the main locale to be edited, edit it
                            scope[entityType].localizedVersions[data.locale] = {};
                            scope[localizedKey] = scope[entityType].localizedVersions[data.locale];

                        } else {
                            // Else edit the main locale
                            scope[localizedKey] = scope[entityType];
                        }
                    });

                }

                return mixin;
            }
        }])

        .factory('entityImageMixin', ['imageService', '$http', '$rootScope', function (imageService, $http, $rootScope) {
            return function (entityType) {
                var mixin = {};

                mixin.editThumbnails = function (image) {
                    $rootScope.$broadcast('thumbnails:edit', entityType, image);
                }

                mixin.reloadImages = function () {
                    var scope = this;
                    $http.get("/api/" + entityType + "s/" + scope.slug + "/images")
                        .success(function (data) {
                            scope[entityType].images = data;
                        }
                    );
                }

                mixin.selectFeatureImage = function (image) {
                    var scope = this;
                    imageService.selectFeatured(scope[entityType], image);
                }

                mixin.removeImage = function (image) {
                    var scope = this;
                    $http.delete("/api/" + entityType + "s/" + scope.slug + "/images/" + image.slug).success(function () {
                        scope.reloadImages();
                    });
                }

                mixin.getImageUploadUri = function () {
                    var scope = this;
                    return "/api/" + entityType + "s/" + scope.slug + "/attachments";
                }

                return mixin;
            }
        }])

        .factory('entityLocalizationService', ['$q', 'configurationService', function ($q, configurationService) {

            var locales,
                promise;

            var getLocales = function () {

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
                            main:generalConfiguration.locales.main,
                            others:generalConfiguration.locales.others
                        };
                        deferred.resolve(locales);
                    });
                }
                return promise;
            }

            return {
                getLocales:getLocales
            };
        }])

        .directive("localized", ['$compile', '$rootScope', 'entityLocalizationService',
            function ($compile, $rootScope, localizationService) {
            return {
                scope:{
                },
                priority:100, // Must execute BEFORE other directives like ck-editor, etc.
                restrict:'A',
                transclude:'element',
                replace:true,
                template:'' +
                    '<div class="locales-wrapper input-append"><div ng-transclude></div>' +
                    '<span class="locales-switch add-on">' +
                    '<div class="btn-group"><a class="btn btn-mini dropdown-toggle" data-toggle="dropdown">' +
                    '<img src="/common/images/flags/{{selectedLocale}}.png"/><span class="caret"></span></a>' +
                    '<ul class="dropdown-menu">' +
                    '<li ng-repeat="locale in locales" ng-click="select(locale)"><img src="/common/images/flags/{{locale}}.png" /></li>' +
                    '</ul>' +
                    '</div>',
                compile:function (element, attrs, transclude) {
                    return {
                        post:function postLink(scope, iElement, iAttrs, controller) {
                            if (iElement.find("textarea").length > 0) {
                                iElement.find(".add-on").removeClass("add-on");
                                iElement.addClass("textarea");
                            }
                        }
                    }
                },

                controller:function ($scope, $element, $attrs) {

                    $scope.select = function (locale) {
                        $scope.selectedLocale = locale;
                    };

                    $scope.$watch('selectedLocale', function (locale, oldLocale) {
                        if (locale !== undefined) {
                            $rootScope.$broadcast("entity:editedLocaleChanged", {
                                "locale":$scope.selectedLocale,
                                "previously":oldLocale,
                                "isMainLocale":$scope.selectedLocale == $scope.mainLocale,
                                "wasMainLocale":$scope.mainLocale === oldLocale
                            });
                        }
                    });

                    $scope.$on("entity:editedLocaleChanged", function (event, data) {
                        if (event.currentScope !== event.targetScope) {
                            $scope.selectedLocale = data.locale
                        }
                    });

                    localizationService.getLocales().then(function (locales) {
                        $scope.mainLocale = locales.main;
                        $scope.selectedLocale = $scope.mainLocale;
                        $scope.locales = [ locales.main ];
                        $scope.locales.push.apply($scope.locales, locales.others);
                    });
                }
            }
        }])

})();