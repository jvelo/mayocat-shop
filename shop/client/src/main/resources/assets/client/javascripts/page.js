/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {

    'use strict'

    angular.module('page', ['ngResource'])

        .controller('PageController', [
        '$scope',
        '$rootScope',
        '$resource',
        '$http',
        '$location',
        '$modal',
        'entityMixins',

        function ($scope, $rootScope, $resource, $http, $location, $modal, entityMixins) {

            entityMixins.extendAll($scope, "page");

            $scope.publishPage = function () {
                $scope.page.published = true;
                $scope.updatePage();
            }

            $scope.updatePage = function () {
                $scope.isSaving = true;
                if ($scope.isNew()) {
                    $http.post("/api/pages/", $scope.page)
                        .success(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            if (status < 400) {
                                var fragments = headers("location").split('/'),
                                    slug = fragments[fragments.length - 1];
                                $rootScope.$broadcast('pages:refreshList');
                                $location.url("/pages/" + slug);
                            }
                            else {
                                if (status === 409) {
                                    $modal.open({ templateUrl: 'conflictError.html' });
                                }
                                else {
                                    // Generic error
                                    $modal.open({ templateUrl: 'serverError.html' });
                                }
                            }
                        })
                        .error(function (data, status, headers, config) {
                            $scope.isSaving = false;
                        });
                }
                else {
                    $scope.PageResource.save({ "slug":$scope.slug }, $scope.page, function () {
                        $scope.isSaving = false;
                        $rootScope.$broadcast('pages:refreshList');
                    });
                }
            };

            $scope.PageResource = $resource("/api/pages/:slug");

            // Initialize existing page or new page

            if (!$scope.isNew()) {
                $scope.page = $scope.PageResource.get({
                    "slug":$scope.slug,
                    "expand":["images"] }, function () {

                    $scope.initializeEntity();

                    if ($scope.page.published == null) {
                        // "null" does not seem to be evaluated properly in angular directives
                        // (like ng-show="something != null")
                        // Thus, we convert "null" published flag to undefined to be able to have that "high impedance"
                        // state in angular directives.
                        $scope.page.published = undefined;
                    }
                });
            }
            else {
                $scope.page = $scope.newPage();

                $scope.initializeEntity();
            }

            $scope.confirmDeletion = function () {
                $scope.modalInstance = $modal.open({ templateUrl: 'confirmDeletionPage.html' });
                $scope.modalInstance.result.then($scope.deletePage);
            }

            $scope.deletePage = function () {
                $scope.PageResource.delete({
                    "slug":$scope.slug
                }, function () {
                    $scope.modalInstance.close();
                    $rootScope.$broadcast('pages:refreshList');
                    $location.url("/contents");
                });
            }

            $scope.getTranslationProperties = function () {
                return {
                    pageTitle: $scope.localizedPage.title,
                    imagesLength: $scope.getNumberOfImages()
                };
            };

        }]);

})();
