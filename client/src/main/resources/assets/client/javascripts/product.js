'use strict'

angular.module('product', ['ngResource'])
  .controller('ProductController', ['$scope', '$routeParams', '$resource', '$location',
      function($scope, $routeParams, $resource, $location) {

        $scope.slug = $routeParams.product;

        $scope.updateProduct = function() {
            if ($scope.isNew()) {
                $resource("/product/").save($scope.product, function(response){
                    $location.path(response.href);
                });
            }
            else {
                $scope.ProductResource.save({ "slug" : $scope.slug }, $scope.product);
            }
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
