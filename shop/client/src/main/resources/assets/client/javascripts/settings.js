/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict';

angular.module('settings', ['ngResource'])

    //==================================================================================================================
    //
    // Controller for the general settings UI
    // See partials/settingsGeneral.html
    //
    .controller('SettingsController', ['$scope', '$rootScope', 'configurationService', 'timeService', 'localesService',

        function ($scope, $rootScope, configurationService, timeService, localesService) {

            // Scope functions -----------------------------------------------------------------------------------------

            $scope.updateSettings = function () {
                $scope.isSaving = true;
                configurationService.put($scope.settings, function () {
                    $scope.isSaving = false;
                    $rootScope.$broadcast("catalog:refreshCatalog");
                });
            };

            $scope.isVisible = function (path) {
                return configurationService.isVisible($scope.settings, path);
            }

            $scope.isConfigurable = function (path) {
                return configurationService.isConfigurable($scope.settings, path);
            }

            $scope.isDefaultValue = function (path) {
                return configurationService.isDefaultValue($scope.settings, path);
            };

            /**
             * Function passed to the list-picker to handle the display of locale tags
             */
            $scope.displayLocale = function () {
                var localesData = $scope.localesData.$$v || {}, // this is morally wrong
                    locales = localesData.locales || [],
                    variants = localesData.variants || [];

                // Check in locales
                for (var i = 0, locale; locale = locales[i++];) {
                    if (locale.tag === $scope.elementToDisplay) {
                        return locale.name;
                    }
                }

                // Check in variants
                for (var j = 0, variant; variant = variants[j++];) {
                    if (variant.tag === $scope.elementToDisplay) {
                        return variant.name;
                    }
                }

                return $scope.elementToDisplay;
            }

            // Initialization ------------------------------------------------------------------------------------------

            $scope.timeZoneRegions = timeService.getTimeZoneData();

            $scope.localesData = localesService.getData();

            configurationService.getSettings(function (settings) {
                $scope.settings = settings;
            });
        }

    ])

    //==================================================================================================================
    //
    // Controller for the tenant (shop information) settings UI
    // See partials/settingsTenant.html
    //
    .controller('SettingsTenantController', ['$scope', '$rootScope', '$location', '$resource', '$http', 'addonsService', 'entityMixins',
        function ($scope, $rootScope, $location, $resource, $http, addonsService, entityMixins) {

            entityMixins.extend(["base", "addons", "image"], $scope, "tenant", {
                "base": {
                    "apiBase": "/api/tenant",
                    "noSlug" : true
                },
                "image" : {
                    /**
                     * After an image is uploaded, preview it as the shop's logo
                     */
                    "afterReloadingImages": function () {
                        $scope.updatedTenant = $scope.TenantResource.get({
                        }, function () {
                            $scope.tenant._embedded.featuredImage = $scope.updatedTenant._embedded.featuredImage;
                        });
                    }
                }
            });

            // Refresh logo when a new one is uploaded -----------------------------------------------------------------

            $rootScope.$on("upload:done", function(event, memo) {
                if (memo.entityUri == $location.path() && memo.id == 'logo') {
                    $scope.reloadImages();
                    $scope.uploadingLogo = false;
                }
            });

            $rootScope.$on("upload:progress", function(event, memo) {
                var index = memo.queue.findIndex(function (upload) {
                    return upload.id == 'logo';
                });
                if (index >= 0) {
                    $scope.uploadingLogo = true;
                }
            });

            $scope.TenantResource = $resource("/api/tenant");

            // Scope functions -----------------------------------------------------------------------------------------

            $scope.initializeAddons = function () {
                addonsService.initializeEntityAddons("tenant", $scope.tenant).then(function (addons) {
                    $scope.addons = addons;
                });
            }

            $scope.updateTenant = function () {
                $scope.isSaving = true;
                $scope.TenantResource.save({}, $scope.tenant, function () {
                    $scope.isSaving = false;
                });
            }

            // Initialization ------------------------------------------------------------------------------------------

            $scope.addons = [];

            $scope.tenant = $scope.TenantResource.get({
            }, function () {
                $scope.initializeEntity();
            });

        }
    ])

    //==================================================================================================================
    //
    // Controller for the shipping settings UI
    // See partials/settingsShipping.html
    //
    .controller('SettingsShippingController', ['$scope', '$http', '$q', '$timeout', '$modal', 'configurationService',
        'shippingService',  function ($scope, $http, $q, $timeout, $modal, configurationService, shippingService) {

            // Scope functions -----------------------------------------------------------------------------------------

            $scope.setStrategy = function (strategy) {
                configurationService.getSettings(function (settings) {
                    settings.shipping.strategy.value = strategy;
                    configurationService.put(settings, function () {
                        $scope.configuration = settings.shipping;
                    })
                })
            };

            $scope.fromValue = function (rules, index) {
                return rules[index] ? (parseFloat(rules[index].upToValue) || "") : 0;
            }

            $scope.isValidFloat = function (numberAsString) {
                return typeof numberAsString == 'undefined' || !isNaN(numberAsString);
            }

            $scope.validShippingDurationRange = function (carrier) {
                if (typeof carrier === "undefined" ||
                    typeof carrier.minimumDays === "undefined" || typeof carrier.maximumDays === "undefined") {
                    return true;
                }
                return !isNaN(carrier.minimumDays) && !isNaN(carrier.maximumDays)
                    && parseFloat(carrier.minimumDays) <= parseFloat(carrier.maximumDays);
            }

            $scope.stopEditingCarrier = function () {
                delete $scope.editedCarrier;
            }

            $scope.editCarrier = function (carrier) {
                $scope.editedCarrier = carrier;
                $scope.addFirstRule(carrier);
            }

            $scope.newCarrierForm = function (strategy) {
                $scope.editedCarrier = {
                    isNew: true,
                    strategy: strategy,
                    rules: []
                };

                $scope.addFirstRule($scope.editedCarrier);
            }

            $scope.addFirstRule = function (carrier) {
                if (carrier.strategy == 'weight' || carrier.strategy == 'price') {
                    carrier.rules.push({});
                }
            };

            $scope.createOrUpdateCarrier = function () {
                if ($scope.editedCarrier.isNew) {
                    $scope.isSaving = true;
                    $http.post("/api/shipping/carrier/", $scope.editedCarrier)
                        .success(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            $scope.stopEditingCarrier();
                            $scope.loadCarriers();
                        })
                        .error(function () {
                            $modal.open({ templateUrl: 'serverError.html' });
                        });
                }
                else {
                    $scope.isSaving = true;
                    $http.put("/api/shipping/carrier/" + $scope.editedCarrier.id, $scope.editedCarrier)
                        .success(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            $scope.stopEditingCarrier();
                            $scope.loadCarriers();

                        })
                        .error(function () {
                            $modal.open({ templateUrl: 'serverError.html' });
                        });
                }
            }

            $scope.deleteCarrier = function(carrier) {
                $http.delete("/api/shipping/carrier/" + carrier.id)
                    .success(function (data, status, headers, config) {
                        $scope.loadCarriers();
                    })
                    .error(function () {
                        $modal.open({ templateUrl: 'serverError.html' });
                    });
            }

            $scope.loadCarriers = function() {
                $scope.carriers = {};
                angular.forEach(["weight", "price", "flat"], function (strategy) {
                    $http.get("/api/shipping/carrier/?strategy=" + strategy)
                        .success(function (carriers) {
                            $scope.carriers[strategy] = carriers;
                            angular.forEach($scope.carriers[strategy], function (carrier) {
                                if (["weight", "price"].indexOf(strategy) >= 0) {
                                    for (var i = 0; i < carrier.rules.length; i++) {
                                        carrier.rules[i].fromValue = i == 0 ? 0 : carrier.rules[i - 1].upToValue;
                                    }
                                }
                                shippingService.getNames(carrier.destinations).then(function (names) {
                                    carrier.displayDestinations = names.join(", ");
                                });
                            });
                        })
                        .error(function () {
                            $modal.open({ templateUrl: 'serverError.html' });
                        });
                });
            }

            $scope.getTranslationProperties = function () {
                var editedCarrier = $scope.editedCarrier || {};
                return {
                    mainCurrency: $scope.mainCurrency || '',
                    weightUnit: $scope.weightUnit || '',
                    numberOfSelectedDestinations: (editedCarrier.destinations || {}).length || 0,
                    maximumDaysSelected: editedCarrier.maximumDays || 0
                };
            };

            $scope.$watch('editedCarrier.destinations', function (path) {
                if (typeof $scope.editedCarrier !== 'undefined') {
                    shippingService.getNames($scope.editedCarrier.destinations).then(function(names){
                        $scope.editedCarrierDisplayDestinations = names.join(", ")
                    });
                }
            });

            // Initialization ------------------------------------------------------------------------------------------

            $scope.strategy = {};
            angular.forEach(["weight", "price", "flat", "none"], function(strategy){
                // Initialize "active" flag for strategies.
                // This is needed for the tabs mechanism to have default active tab selection work properly
                $scope.strategy[strategy] = { active: false };
            });

            configurationService.getSettings("shipping", function (shippingConfiguration) {
                $scope.configuration = shippingConfiguration;
                $timeout(function(){
                    // Hackery:
                    // Initialize tab status in a timeout callback so that they are initialized AFTER tabset directive init
                    // There must be a better way to do this though...
                    $scope.$apply(function($scope){
                        angular.forEach(["weight", "price", "flat", "none"], function(strategy){
                            // Update "active" flag now that we know the strategy configured for this shop;
                            $scope.strategy[strategy].active = ($scope.configuration.strategy.value == strategy);
                        });
                    });
                });
            });

            configurationService.get("catalog", function(catalogSettings){
                $scope.mainCurrency = catalogSettings.currencies.main;
                $scope.weightUnit = catalogSettings.products.weightUnit;
            });

            $scope.loadCarriers();
        }])

    //==================================================================================================================
    //
    // Controller for the settings sub-menu
    // See partials/settingsMenu.html
    //
    .controller('SettingsMenuController', ['$scope', '$location',

        function ($scope, $location) {

            $scope.isGeneral = false;
            $scope.isTenant = false;
            $scope.isCatalog = false;
            $scope.isShipping = false;

            $scope.$watch('location.path()', function (path) {

                $scope.isGeneral = false;
                $scope.isTenant = false;
                $scope.isCatalog = false;
                $scope.isShipping = false;

                if (path === "/settings/") {
                    $scope.isGeneral = true;
                }
                if (path === "/settings/tenant") {
                    $scope.isTenant = true;
                }
                if (path === "/settings/catalog") {
                    $scope.isCatalog = true;
                }
                if (path === "/settings/shipping") {
                    $scope.isShipping = true;
                }
            });

            $scope.setRoute = function (href) {
                $location.url(href);
            };
        }

    ]);
