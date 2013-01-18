'use strict'

angular.module('category', ['ngResource'])
  .controller('CategoryController', ['$scope', '$routeParams', '$resource',
      function($scope, $routeParams, $resource) {

        $scope.slug = $routeParams.category;

        $scope.updateCategory = function() {
          $scope.CategoryResource.save({ "slug" : $scope.slug }, $scope.category);
        }

        $scope.CategoryResource = $resource("/category/:slug");

        $scope.category = $scope.CategoryResource.get({ "slug" : $scope.slug });
  }]);
