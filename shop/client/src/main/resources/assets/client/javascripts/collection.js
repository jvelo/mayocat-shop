'use strict'

angular.module('collection', ['ngResource'])
  .controller('CollectionController', ['$scope', '$routeParams', '$resource',
      function($scope, $routeParams, $resource) {

        $scope.slug = $routeParams.collection;

        $scope.updateCollection = function() {
          $scope.CollectionResource.save({ "slug" : $scope.slug }, $scope.collection);
        }

        $scope.CollectionResource = $resource("/api/1.0/collection/:slug");

        $scope.collection = $scope.CollectionResource.get({ "slug" : $scope.slug });
  }]);
