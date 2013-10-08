'use strict'

angular.module('product', ['ngResource'])

    .controller('ProductController', [
        '$scope',
        '$rootScope',
        '$routeParams',
        '$resource',
        '$http',
        '$location',
        'catalogService',
        'configurationService',
        'entityMixins',

        function ($scope,
                  $rootScope,
                  $routeParams,
                  $resource,
                  $http,
                  $location,
                  catalogService,
                  configurationService,
                  entityMixins) {

            entityMixins.extendAll($scope, "product");

            $scope.publishProduct = function () {
                $scope.product.onShelf = true;
                $scope.updateProduct();
            }

            $scope.updateProduct = function () {
                if ($scope.isNew()) {
                    $scope.isSaving = true;
                    $http.post("/api/products/", $scope.product)
                        .success(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            if (status < 400) {
                                var fragments = headers("location").split('/'),
                                    slug = fragments[fragments.length - 1];
                                $rootScope.$broadcast('catalog:refreshCatalog');
                                $location.url("/products/" + slug);
                            }
                            else {
                                if (status === 409) {
                                    $rootScope.$broadcast('event:nameConflictError');
                                }
                                else {
                                    // Generic error
                                    $rootScope.$broadcast('event:serverError');
                                }
                            }
                        })
                        .error(function (data, status, headers, config) {
                            $scope.$parent.$broadcast('event:serverError');
                            $scope.isSaving = false;
                        });
                }
                else {
                    $scope.isSaving = true;
                    $scope.ProductResource.save({ "slug": $scope.slug }, $scope.product, function () {
                        $scope.isSaving = false;
                        $rootScope.$broadcast('catalog:refreshCatalog');
                    });
                    angular.forEach($scope.collections, function (collection) {
                        if (collection.hasProduct && !collection.hadProduct) {
                            $scope.collectionOperation(collection, "addProduct");
                        }
                        if (collection.hadProduct && !collection.hasProduct) {
                            $scope.collectionOperation(collection, "removeProduct");
                        }
                    });
                }
            };

            $scope.collectionOperation = function (collection, operation) {
                $resource("/api/collections/:slug/:operation", {"slug": collection.slug, "operation": operation}, {
                    "save": {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    }
                }).save("product=" + $scope.product.slug, function () {
                    });
            };

            $scope.ProductResource = $resource("/api/products/:slug");

            $scope.initializeCollections = function () {
                catalogService.hasCollections(function (hasCollections) {
                    $scope.hasCollections = hasCollections;
                });

                catalogService.listCollections(function (collections) {
                    $scope.collections = collections;
                    angular.forEach($scope.collections, function (collection) {
                        angular.forEach($scope.product.collections, function (c) {
                            if (collection.href == c.href) {
                                // hasProduct => used as model
                                collection.hasProduct = true
                                // hadProduct => used when saving to see if we need to update anything
                                collection.hadProduct = true
                            }
                            else if (!collection.hasProduct) {
                                collection.hasProduct = false;
                                collection.hadProduct = false;
                            }
                        });
                    });
                });
            }

            configurationService.get("catalog", function (catalogConfiguration) {
                $scope.hasWeight = catalogConfiguration.products.weight;
                $scope.weightUnit = catalogConfiguration.products.weightUnit;
                $scope.hasStock = catalogConfiguration.products.stock;
            });

            // Initialize existing product or new product

            if (!$scope.isNew()) {
                $scope.product = $scope.ProductResource.get({
                    "slug": $scope.slug,
                    "expand": ["collections", "images"] }, function () {
                    $scope.reloadImages();

                    // Ensures the collection initialization happens after the AJAX callback
                    $scope.initializeCollections();

                    // Same for addons
                    $scope.initializeEntity();

                    if ($scope.product.onShelf == null) {
                        // "null" does not seem to be evaluated properly in angular directives
                        // (like ng-show="something != null")
                        // Thus, we convert "null"onShelf to undefined to be able to have that "high impedance" state in
                        // angular directives.
                        $scope.product.onShelf = undefined;
                    }
                });
            }
            else {
                $scope.product = $scope.newProduct();

                $scope.initializeEntity();
            }

            $scope.confirmDeletion = function () {
                $rootScope.$broadcast('product:confirmDelete');
            }

            $scope.deleteProduct = function () {
                $scope.ProductResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $rootScope.$broadcast('product:dismissConfirmDelete');
                    $rootScope.$broadcast('catalog:refreshCatalog');
                    $location.url("/catalog");
                });
            }

            $scope.getTranslationProperties = function () {
                return {
                    imagesLength: (($scope.product || {}).images || {}).length || 0
                };
            };

        }])
;
