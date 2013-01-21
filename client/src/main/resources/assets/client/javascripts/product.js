'use strict'

angular.module('product', ['ngResource'])
    .controller('ProductController', ['$scope', '$routeParams', '$resource', '$location', 'catalogService',
    function ($scope, $routeParams, $resource, $location, catalogService) {

        $scope.slug = $routeParams.product;

        $scope.updateProduct = function () {
            if ($scope.isNew()) {
                $resource("/product/").save($scope.product, function (response) {
                    $location.path(response.href);
                });
            }
            else {
                $scope.ProductResource.save({ "slug":$scope.slug }, $scope.product);
            }
        };

        $scope.ProductResource = $resource("/product/:slug");

        $scope.isNew = function () {
            return $scope.slug == "_new";
        };

        $scope.newProduct = function () {
            return {
                slug:"",
                title:""
            };
        }

        if (!$scope.isNew()) {
            $scope.product = $scope.ProductResource.get({ "slug":$scope.slug, "expand":"categories" }, function () {
                // Ensures the category initialization happens after the AJAX callback
                $scope.initializeCategories();
            })
        }
        else {
            $scope.product = $scope.newProduct();
            $scope.initializeCategories();
        }

        $scope.initializeCategories = function () {
            catalogService.listCategories(function (categories) {
                $scope.categories = categories;
                angular.forEach($scope.categories, function (category) {
                    angular.forEach($scope.product.categories, function (c) {
                        if (category.href == c.href) {
                            // hasProduct => used as model
                            category.hasProduct = true
                            // hadProduct => used when saving to see if we need to update anything
                            category.hadProduct = true
                        }
                        else if (!category.hasProduct) {
                            category.hasProduct = false;
                            category.hadProduct = false;
                        }
                    });
                });
            });
        }

    }]);
