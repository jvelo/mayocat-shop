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
                var locales = $scope.locales.$$v; // this is morally wrong
                for (var i = 0; i < locales.length; i++) {
                    if (locales[i].tag === $scope.elementToDisplay) {
                        return locales[i].name;
                    }
                }
                return $scope.elementToDisplay;
            }

            // Initialization ------------------------------------------------------------------------------------------

            $scope.timeZoneRegions = timeService.getTimeZoneData();

            $scope.locales = localesService.getData();

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
    .controller('SettingsTenantController', ['$scope', '$resource', '$http', 'addonsService', 'imageService',
        function ($scope, $resource, $http, addonsService, imageService) {

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

            $scope.reloadImages = function (file) {
                // Reload list of images
                $scope.tenant.images = $http.get("/api/tenant/images").success(function (data) {
                    $scope.tenant.images = data;
                });
                // Reload featured image
                var tenant = $scope.TenantResource.get({
                }, function () {
                    $scope.tenant.featuredImage = tenant.featuredImage;
                });
            }

            $scope.selectFeatureImage = function (image) {
                imageService.selectFeatured($scope.product, image);
            }

            // Initialization ------------------------------------------------------------------------------------------

            $scope.addons = [];

            $scope.TenantResource = $resource("/api/tenant/");

            $scope.tenant = $scope.TenantResource.get({
            }, function () {
                $scope.initializeAddons();
            });

        }
    ])

    //==================================================================================================================
    //
    // Controller for the shipping settings UI
    // See partials/settingsShipping.html
    //
    .controller('SettingsShippingController', ['$scope', '$http', '$q', '$timeout', 'configurationService', 'shippingService',
        function ($scope, $http, $q, $timeout, configurationService, shippingService) {

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
                return !isNaN(numberAsString);
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
            }

            $scope.newCarrierForm = function (strategy) {
                $scope.editedCarrier = {
                    isNew: true,
                    strategy: strategy,
                    rules: []
                };
            }

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
                            $scope.$parent.$broadcast('event:serverError');
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
                            $scope.$parent.$broadcast('event:serverError');
                        });
                }
            }

            $scope.deleteCarrier = function(carrier) {
                $http.delete("/api/shipping/carrier/" + carrier.id)
                    .success(function (data, status, headers, config) {
                        $scope.loadCarriers();
                    })
                    .error(function () {
                        $scope.$parent.$broadcast('event:serverError');
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
                            $scope.$parent.$broadcast('event:serverError');
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
