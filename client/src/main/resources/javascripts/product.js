'use strict'

angular.module('product', ['ngResource'])
  .controller('ProductController', ['$scope', '$routeParams', '$resource',
      function($scope, $routeParams, $resource) {
        $scope.handle = $routeParams.product;
        this.Product = $resource("/product/:handle");
        $scope.product = this.Product.get({ "handle" : $scope.handle });
  }]);
