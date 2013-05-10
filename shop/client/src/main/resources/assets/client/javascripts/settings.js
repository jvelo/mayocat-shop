'use strict';

angular.module('settings', ['ngResource'])
    .controller('SettingsController', ['$scope', '$rootScope', 'configurationService', 'timeService',

        function ($scope, $rootScope, configurationService, timeService) {

            $scope.updateSettings = function () {
                $scope.isSaving = true;
                configurationService.put($scope.settings, function () {
                    $scope.isSaving = false;
                    $rootScope.$broadcast("catalog:refreshCatalog");
                });
            };

            $scope.timeZoneRegions = timeService.getTimeZoneData();

            $scope.isVisible = function (path) {
                return configurationService.isVisible($scope.settings, path);
            }

            $scope.isConfigurable = function (path) {
                return configurationService.isConfigurable($scope.settings, path);
            }

            $scope.isDefaultValue = function (path) {
                return configurationService.isDefaultValue($scope.settings, path);
            };

            configurationService.getSettings(function (settings) {
                $scope.settings = settings;
            });
        }

    ])

    .controller('SettingsTenantController', ['$scope', '$resource', 'addonsService',
        function ($scope, $resource, addonsService) {

            $scope.addons = [];
            $scope.TenantResource = $resource("/api/tenants/_current");

            $scope.initializeAddons = function () {
                addonsService.initialize("tenant", $scope.tenant).then(function (addons) {
                    $scope.addons = addons;
                });
            }

            $scope.updateTenant = function () {
                $scope.isSaving = true;
                $scope.TenantResource.save({}, $scope.tenant, function () {
                    $scope.isSaving = false;
                });
            }

            $scope.tenant = $scope.TenantResource.get({
            }, function () {
                $scope.tenant = $scope.tenant.tenant;
                $scope.initializeAddons();
            });

        }
    ])

    .controller('SettingsMenuController', ['$scope', '$location',

        function ($scope, $location) {
            $scope.isGeneral = false;
            $scope.isTenant = false;
            $scope.isCatalog = false;

            $scope.$watch('location.path()', function (path) {

                $scope.isGeneral = false;
                $scope.isTenant = false;
                $scope.isCatalog = false;

                if (path === "/settings/") {
                    $scope.isGeneral = true;
                }
                if (path === "/settings/tenant") {
                    $scope.isTenant = true;
                }
                if (path === "/settings/catalog") {
                    $scope.isCatalog = true;
                }

            });

            $scope.setRoute = function (href) {
                $location.url(href);
            };
        }

    ]);
