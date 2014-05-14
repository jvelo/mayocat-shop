/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('TenantManager.tenants', [])

    //==================================================================================================================
    //
    // Controller for the list/table of tenants
    // See partials/tenants.html
    //
    .controller('TenantListController', ['$scope', '$resource', '$http',
        function ($scope, $resource, $http) {

            // Constants -----------------------------------------------------------------------------------------------

            var itemsPerPage = 10;

            // Scope variables -----------------------------------------------------------------------------------------

            $scope.currentPage = 1;
            $scope.totalPages = 0;
            $scope.pages = [];

            // Scope functions -----------------------------------------------------------------------------------------

            $scope.gotoPage = function (page) {
                $scope.currentPage = page;
                $scope.fetchTenants();
            }

            $scope.fetchTenants = function () {
                var number = itemsPerPage,
                    offset = ($scope.currentPage - 1) * itemsPerPage;

                $scope.tenants = [];
                $scope.loading = true;

                $http.get("/management/api/tenants?number=" + number + "&offset=" + offset).success(function (result) {
                    var number = result._pagination.numberOfItems,
                        numberOfPages = Math.floor(result._pagination.totalItems / number);
                    numberOfPages += (result._pagination.totalItems % number === 0 ? 0 : 1);

                    $scope.totalPages = numberOfPages;
                    for (var i = 0; i < numberOfPages; i++) {
                        $scope.pages[i] = {
                            number: i + 1,
                            href: "?number=" + number + "&offset=" + (numberOfPages * number)
                        };
                    }

                    $scope.tenants = result.tenants;
                    $scope.loading = false;
                });
            }

            // Initialization ------------------------------------------------------------------------------------------

            $scope.fetchTenants();

        }])

    //==================================================================================================================
    //
    // A directive used for ensuring an input value is a valid slug.
    //
    .directive('requireValidSlug', [function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, controller) {
                controller.$parsers.unshift(function (viewValue) {
                    if (viewValue.match(/^[a-z0-9-]+$/)) {
                        controller.$setValidity(attrs.ngModel || attrs.name, true);
                        return viewValue;
                    }
                    else {
                        controller.$setValidity(attrs.ngModel || attrs.name, false);
                        return undefined;
                    }
                });
            }
        }
    }])

    //==================================================================================================================
    //
    // A directive used for validating a password with a second input field.
    // Sample usage:
    // <input type="text" name="password" ng-model="user.password" required />
    // <input type="text" name="password2" verify-password="user.password" />
    //
    .directive('confirmPassword', [function () {
        return {
            restrict: 'A',
            require: 'ngModel',
            link: function (scope, element, attrs, controller) {
                var inputToVerify = attrs.confirmPassword,
                    getObjectPropertyFromPath = function (obj, path) {
                        var props = path.split(".");
                        return props.reduce(function (memo, prop) {
                            return memo && memo[prop];
                        }, obj);
                    };
                controller.$parsers.unshift(function (viewValue) {
                    if (viewValue === getObjectPropertyFromPath(scope, inputToVerify)) {
                        controller.$setValidity(attrs.ngModel || attrs.name, true);
                        return viewValue;
                    }
                    else {
                        controller.$setValidity(attrs.ngModel || attrs.name, false);
                        return undefined;
                    }
                });
            }
        }
    }])

    //==================================================================================================================
    //
    // Controller for the "new tenant" form.
    // See partials/newTenant.html
    //
    .controller('NewTenantController', ['$scope', '$http', '$location',
        function ($scope, $http, $location) {

            // Helpers -------------------------------------------------------------------------------------------------

            /**
             * Slugifies a string
             * @param text the string to slugify
             * @returns {*} the slugified text, or undefined if the passed string was not defined
             */
            var slugify = function (text) {
                if (typeof text !== 'undefined') {
                    text = text.replace(/[^-a-zA-Z0-9_\-,&\s]+/ig, '');
                    text = text.replace(/\s/gi, "-");
                    text = text.toLowerCase();
                }
                return text;
            }

            // Scope variables -----------------------------------------------------------------------------------------

            $scope.tenant = {};
            $scope.user = {};
            $scope.isEditingSlug = false;

            // Scope functions -----------------------------------------------------------------------------------------

            $scope.createTenant = function () {
                $scope.loading = true;
                $http.post("/management/api/tenants/", {
                    "user": $scope.user,
                    "tenant": $scope.tenant
                }).success(function (data, status, headers, config) {
                        if (status >= 200 && status < 400) {
                            $location.url('/');
                        }
                        else {
                            $scope.globalError = data;
                            if (status == 422) {
                                // Validation error

                            }
                        }
                    });
            }

            $scope.isInvalid = function (field) {
                return $scope.newTenant[field].$invalid && $scope.newTenant[field].$dirty;
            };

            $scope.isValid = function (field) {
                return $scope.newTenant[field].$valid && $scope.newTenant[field].$dirty;
            };

            // Watches -------------------------------------------------------------------------------------------------

            $scope.$watch('tenant.name', function () {
                if (!$scope.isEditingSlug) {
                    $scope.tenant.slug = slugify($scope.tenant.name);
                }
            });

            $scope.$watch('tenant.slug', function (newValue) {
                if ($scope.isEditingSlug) {
                    $scope.tenant.slug = slugify(newValue);
                }
            });

        }])

    //==================================================================================================================
    //
    // Controller for a single tenant
    // See partials/tenant.html
    //
    .controller('TenantController', ['$scope', '$routeParams', '$resource', 'addonsService',
        function ($scope, $routeParams, $resource, addonsService) {

            // Scope variables -----------------------------------------------------------------------------------------

            $scope.slug = $routeParams.tenant;
            $scope.TenantResource = $resource("/management/api/tenants/:slug");
            $scope.tenant = $scope.TenantResource.get({"slug": $scope.slug },
                function () {
                    addonsService.initializeEntityAddons("tenant", $scope.tenant).then(function (addons) {
                        $scope.addons = addons;
                    });
                });

            // Scope functions -----------------------------------------------------------------------------------------

            $scope.updateTenant = function () {
                $scope.isLoading = true;
                $scope.TenantResource.save({"slug": $scope.slug }, $scope.tenant, function () {
                    $scope.isLoading = false;
                });
            }
        }]);


//======================================================================================================================
//
// Application initialization
//
var TenantManager = angular.module('TenantManager', [
    'mayocat',
    'TenantManager.tenants',
    'ui.bootstrap'
]);

TenantManager.controller("ManagerController", ['$scope', 'configurationService', '$location',

    function ($scope, configurationService, location) {

        // Helpers -----------------------------------------------------------------------------------------------------

        /**
         * Djb2 algorithm. Used below in #stringToHexColor
         *
         * See http://erlycoder.com/49/javascript-hash-functions-to-convert-string-into-integer-hash-
         *
         * @param str
         * @returns {number}
         */
        var djb2 = function (str) {
            var hash = 5381;
            for (var i = 0; i < str.length; i++) {
                hash = ((hash << 5) + hash) + str.charCodeAt(i);
                /* hash * 33 + c */
            }
            return hash;
        }

        // Scope functions ---------------------------------------------------------------------------------------------

        /**
         * Converts a string to a hex color
         *
         * @param str the string to convert
         * @returns {number} the hex color for this string (derived from a hash of it)
         */
        $scope.stringToHexColor = function (str) {
            var hash = djb2(str);
            var r = ((hash & 0xFF0000) >> 16);
            var g = ((hash & 0x00FF00) >> 8);
            var b = (hash & 0x0000FF);
            return [r, g, b].reduce(function (memo, value) {
                var hex = value.toString(16);
                return memo + ((hex.length === 1 ? "0" : "") + hex);
            }, '#');
        }

        $scope.setRoute = function (href) {
            location.url(href);
        };

        $scope.$watch(function () {
            return location.path()
        }, function (path) {
            $scope.isHome = false;
            if (path == '/') {
                $scope.isHome = true;
            }
        });

        // Scope initialization ----------------------------------------------------------------------------------------

        configurationService.get("site.domain", function (value) {
            $scope.domain = value;
        });

    }]);

TenantManager.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.
        when('/', {templateUrl: 'partials/tenants.html', controller: 'TenantListController'}).
        when('/tenants/_new', {templateUrl: 'partials/newTenant.html', controller: 'NewTenantController'}).
        when('/tenants/:tenant', {templateUrl: 'partials/tenant.html', controller: 'TenantController'}).
        otherwise({redirectTo: '/'});
}]);
