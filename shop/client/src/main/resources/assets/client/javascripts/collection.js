/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('collection', ['ngResource'])
    .controller('CollectionController', [
        '$scope',
        '$rootScope',
        '$routeParams',
        '$resource',
        '$location',
        '$http',
        '$modal',
        'entityMixins',
        function ($scope, $rootScope, $routeParams, $resource, $location, $http, $modal, entityMixins) {

            entityMixins.extend(["base", "localization", "image"], $scope, "collection");

            $scope.CollectionResource = $resource("/api/collections/:slug");

            // Functions

            $scope.updateCollection = function (callback) {
                $scope.isSaving = true;
                if ($scope.isNew()) {
                    $http.post("/api/collections/", $scope.collection)
                        .success(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            if (status < 400) {
                                var fragments = headers("location").split('/'),
                                    slug = fragments[fragments.length - 1];
                                $location.url("/collections/" + slug);
                                $rootScope.$broadcast("catalog:refreshCatalog");
                                callback && callback.call();
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
                            callback && callback.call();
                        });
                }
                else {
                    $scope.CollectionResource.save({ "slug": $scope.slug }, $scope.collection, function () {
                        $scope.isSaving = false;
                        $rootScope.$broadcast('catalog:refreshCatalog');
                    });
                }
            }

            $scope.confirmDeletion = function () {
                $scope.modalInstance = $modal.open({ templateUrl: 'confirmDeletionCollection.html' });
                $scope.modalInstance.result.then($scope.deleteCollection);
            }

            $scope.deleteCollection = function () {
                $scope.CollectionResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $scope.modalInstance.close();
                    $rootScope.$broadcast('catalog:refreshCatalog');
                    $location.url("/catalog");
                });
            }

            $scope.getTranslationProperties = function () {
                return {
                    collectionTitle: $scope.localizedCollection.title,
                    imagesLength: $scope.getNumberOfImages()
                };
            };

            // Initialize

            if (!$scope.isNew()) {
                $scope.collection = $scope.CollectionResource.get({
                    "slug": $scope.slug }, function () {

                    $scope.reloadImages();

                    $scope.initializeEntity();
                });
            }
            else {
                $scope.collection = $scope.newCollection();

                $scope.initializeEntity();
            }

        }]);
