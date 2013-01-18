'use strict'

angular.module('product', ['ngResource'])
  .controller('ProductController', ['$scope', '$routeParams', '$resource',
      function($scope, $routeParams, $resource) {

        $scope.slug = $routeParams.product;

        $scope.updateProduct = function() {
          $scope.ProductResource.save({ "slug" : $scope.slug }, $scope.product);
        };

        $scope.ProductResource = $resource("/product/:slug");

        $scope.isNew = function(){
          return $scope.slug == "_new";
        };

        $scope.newProduct = function(){
          return {
            slug: "",
            title: ""
          };
        }

        $scope.product = !$scope.isNew() ?
          $scope.ProductResource.get({ "slug" : $scope.slug }) :
          $scope.newProduct();
  }]);
