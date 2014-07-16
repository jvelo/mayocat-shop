/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {

    'use strict'

    var capitalize = function (string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };

    angular.module('mayocat.entities', [])

        .factory('entityMixins', [
            'mixins',
            'entityBaseMixin',
            'entityModelMixin',
            'entityAddonsMixin',
            'entityLocalizationMixin',
            'entityImageMixin',
            function (mixins, base, model, addons, localization, image) {
                return mixins({
                    base: base,
                    model: model,
                    addons: addons,
                    localization: localization,
                    image: image
                });
            }
        ])

        .factory('entityBaseMixin', ["$routeParams" , "$rootScope", function ($routeParams, $rootScope) {
            return function (entityType, options) {
                options = typeof options === "undefined" ? {} : options;

                var slug = $routeParams[entityType];

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
                        scope.initializeImages && scope.initializeImages();
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

                $rootScope.entity = {
                    type: entityType,
                    uri: (options.apiBase || "/api/" + entityType + "s/") + (options.noSlug ? "" : slug),
                    slug: slug
                };
                $rootScope.$broadcast("entity:initialized", $rootScope.entity);

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

                mixin.removeSequenceAddonItem = function (group, index) {
                    var $scope = this,
                        localizedKey = "localized" + capitalize(entityType);
                    $scope[entityType].addons[group.key].value.splice(index, 1);

                    if (typeof $scope[entityType]._localized !== 'undefined') {
                        Object.keys($scope[entityType]._localized).forEach(function (locale) {
                            $scope[entityType]._localized[locale].addons[group.key].value.splice(index, 1);
                        });
                    }
                }

                mixin.addSequenceAddonItem = function (group) {
                    var $scope = this,
                        localizedKey = "localized" + capitalize(entityType);
                    $scope[entityType].addons[group.key].value.push(group.getValueShell());

                    if (typeof $scope[entityType]._localized !== 'undefined') {
                        Object.keys($scope[entityType]._localized).forEach(function (locale) {
                            $scope[entityType]._localized[locale].addons[group.key].value.push(group.getValueShell());
                        });
                    }
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
                        if (typeof scope[entityType] === "undefined" ||
                            (typeof scope[entityType].$resolved !== 'undefined' && !scope[entityType].$resolved)) {
                            // We are not ready
                            return;
                        }

                        if (typeof scope[entityType]._localized === "undefined") {
                            scope[entityType]._localized = {};
                        }

                        if (typeof scope[entityType]._localized[data.locale] !== 'undefined' && !data.isMainLocale) {
                            // If there is a localized version with the new locale to be edited, then use it
                            scope[localizedKey] = scope[entityType]._localized[data.locale];
                            scope.editedLocalePrefix = '/' + data.locale;
                        }
                        else if (!data.isMainLocale) {
                            // Else if it's not the main locale to be edited, edit it
                            scope[entityType]._localized[data.locale] = {};
                            scope[localizedKey] = scope[entityType]._localized[data.locale];
                            scope.editedLocalePrefix = '/' + data.locale;

                        } else {
                            // Else edit the main locale
                            scope[localizedKey] = scope[entityType];
                            scope.editedLocalePrefix = ''
                        }
                    });

                }

                return mixin;
            }
        }])

        .factory('entityImageMixin', ['$http', '$rootScope', '$modal', '$location', '$translate', 'notificationService',
            function ($http, $rootScope, $modal, $location, $translate, notificationService) {
            return function (entityType, options) {
                var mixin = {};

                options = typeof options === "undefined" ? {} : options;

                mixin.initializeImages = function () {
                    var $scope = this;
                    $rootScope.$on("upload:done", function(event, memo) {
                        if (memo.entityUri == $location.path() && memo.id == 'image-gallery') {
                            $scope.reloadImages();
                        }
                    });
                }

                mixin.getImagesSortableOptions = function () {
                    var $scope = this;
                    return {
                        update: function (e, ui) {
                            $scope.saveImagesGallery();
                        }
                    }
                };

                mixin.saveImagesGallery = function () {
                    var $scope = this;
                    $http.post($scope[entityType]._links.images.href, {
                        images: $scope[entityType]._embedded.images
                    }).success(function () {
                        $scope.imagesDirty = false;
                        notificationService.notify($translate('image.status.moved'));
                    });
                }

                mixin.getNumberOfImages = function () {
                    var $scope = this;
                    if (typeof $scope[entityType] !== 'undefined' &&
                        typeof $scope[entityType]._embedded !== 'undefined' &&
                        typeof $scope[entityType]._embedded.images !== 'undefined') {

                        return $scope[entityType]._embedded.images.length;
                    }
                    return 0;
                }

                mixin.editImage = function (image) {
                    var scope = this,
                        modalScope = $rootScope.$new(true);
                    modalScope.entityType = entityType;
                    modalScope.image = image;

                    scope.modalInstance = $modal.open({
                        templateUrl: '/common/partials/editImage.html?3',
                        windowClass: 'editImage',
                        controller: 'ImageEditorController',
                        scope: modalScope
                    });
                    scope.modalInstance.result.then(function () {
                        scope.reloadImages();
                    });
                }

                mixin.reloadImages = function () {
                    var scope = this;
                    $http.get($rootScope.entity.uri + "/images")
                        .success(function (data) {

                            if (typeof scope[entityType]._embedded !== "undefined") {
                                scope[entityType]._embedded.images = data;
                            }

                            // Optional callback if any
                            typeof options.afterReloadingImages === 'function'
                                && options.afterReloadingImages(scope[entityType].images);
                        });
                }

                mixin.selectFeatureImage = function (image) {
                    var scope = this,
                        entity = scope[entityType];

                    for (var img in entity._embedded.images) {
                        if (entity._embedded.images.hasOwnProperty(img)) {
                            if (entity._embedded.images[img]._href === image._href) {
                                entity._embedded.images[img].featured = true;
                            }
                            else {
                                entity._embedded.images[img].featured = false;
                            }
                        }
                    }

                    scope.saveImagesGallery();
                }

                mixin.removeImage = function (image) {
                    var scope = this;
                    $http.delete($rootScope.entity.uri + "/images/" + image.slug).success(function () {
                        scope.reloadImages();
                    });
                }

                mixin.getImageUploadUri = function () {
                    var scope = this;
                    return $rootScope.entity ? ($rootScope.entity.uri + "/attachments") : "";
                }

                return mixin;
            }
        }])

        .factory('entityLocalizationService', ['$q', '$rootScope', 'configurationService', function ($q, $rootScope, configurationService) {

            var locales,
                promise;

            $rootScope.$on("configuration:updated", function () {
                locales = null;
                promise = null;
                $rootScope.$broadcast("entities:locales:changed");
            });

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
                    '<div class="btn-group"><a class="btn dropdown-toggle" data-toggle="dropdown">' +
                    '<img ng-src="/common/images/flags/{{selectedLocale}}.png"/> <span class="caret"></span></a>' +
                    '<ul class="dropdown-menu">' +
                    '<li ng-repeat="locale in locales" ng-click="select(locale)"><img ng-src="/common/images/flags/{{locale}}.png" /></li>' +
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

                    function initLocales($scope) {
                        localizationService.getLocales().then(function (locales) {
                            $scope.mainLocale = locales.main;
                            $scope.selectedLocale = $scope.mainLocale;
                            $scope.locales = [ locales.main ];
                            $scope.locales.push.apply($scope.locales, locales.others);

                            if ($scope.locales.length === 1) {
                                // If there is only one locale, we don't display a locale switcher, but the plain field
                                $($element).removeClass("locales-wrapper input-append");
                                $($element).find(".locales-switch").addClass("hidden");
                            }
                        });
                    }

                    initLocales($scope);

                    $rootScope.$on("entities:locales:changed", function () {
                            initLocales($scope);
                    });

                    // Add a class to the wrapper when the dropdown is displayed.
                    var $btnGroup = $element.find('.locales-switch .btn-group'),
                        observer = new MutationObserver(function() {
                            $element.toggleClass('locales-active', $btnGroup.hasClass('open'));
                        });

                    observer.observe($btnGroup.get(0), {
                        attributes: true,
                        childList: false,
                        characterData: false,
                        subtree: false,
                        attributeFilter: ['class']
                    });
                }
            }
        }])

})();
