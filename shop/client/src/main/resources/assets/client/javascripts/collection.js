'use strict'

angular.module('collection', ['ngResource'])
    .controller('CollectionController', ['$scope', '$rootScope', '$routeParams', '$resource', '$location',
        function ($scope, $rootScope, $routeParams, $resource, $location) {

            $scope.slug = $routeParams.collection;

            $scope.updateCollection = function () {
                $scope.CollectionResource.save({ "slug": $scope.slug }, $scope.collection);
            }

            $scope.CollectionResource = $resource("/api/1.0/collection/:slug");

            $scope.collection = $scope.CollectionResource.get({ "slug": $scope.slug });

            $scope.confirmDeletion = function () {
                $rootScope.$broadcast('collection:confirmDelete');
            }

            $scope.deleteProduct = function () {
                $scope.CollectionResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $rootScope.$broadcast('collection:dismissConfirmDelete');
                    $location.url("/catalog");
                });
            }

        }]);
