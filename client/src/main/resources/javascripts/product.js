'use strict'

angular.module('product', ['ngResource'])
  .controller('ProductController', ['$scope', '$routeParams', '$resource',
      function($scope, $routeParams, $resource) {

        $scope.handle = $routeParams.product;

        $scope.updateProduct = function() {
          $scope.ProductResource.save({ "handle" : $scope.handle }, $scope.product);
        }

        $scope.ProductResource = $resource("/product/:handle");

        $scope.product = $scope.ProductResource.get({ "handle" : $scope.handle });
  }]);
