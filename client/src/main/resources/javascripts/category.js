'use strict'

angular.module('category', ['ngResource'])
  .controller('CategoryController', ['$scope', '$routeParams', '$resource',
      function($scope, $routeParams, $resource) {

        $scope.handle = $routeParams.category;

        $scope.updateCategory = function() {
          $scope.CategoryResource.save({ "handle" : $scope.handle }, $scope.category);
        }

        $scope.CategoryResource = $resource("/category/:handle");

        $scope.category = $scope.CategoryResource.get({ "handle" : $scope.handle });
  }]);
