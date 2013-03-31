'use strict'

angular.module('collection', ['ngResource'])
    .controller('CollectionController', ['$scope', '$rootScope', '$routeParams', '$resource', '$location',
        function ($scope, $rootScope, $routeParams, $resource, $location) {

            $scope.slug = $routeParams.collection;

            $scope.updateCollection = function () {
                $scope.isSaving = true;
                $scope.CollectionResource.save({ "slug": $scope.slug }, $scope.collection, function () {
                    $scope.isSaving = false;
                    $rootScope.$broadcast('catalog:refreshCatalog');
                });
            }

            $scope.CollectionResource = $resource("/api/collections/:slug");

            $scope.collection = $scope.CollectionResource.get({ "slug": $scope.slug });

            $scope.confirmDeletion = function () {
                $rootScope.$broadcast('collection:confirmDelete');
            }

            $scope.deleteProduct = function () {
                $scope.CollectionResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $rootScope.$broadcast('collection:dismissConfirmDelete');
                    $rootScope.$broadcast('catalog:refreshCatalog');
                    $location.url("/catalog");
                });
            }

        }]);
