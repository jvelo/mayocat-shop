/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('product', ['ngResource'])

    .controller('ProductController', [
        '$scope',
        '$rootScope',
        '$routeParams',
        '$resource',
        '$http',
        '$location',
        '$modal',
        'catalogService',
        'configurationService',
        'addonsService',
        'entityMixins',

        function ($scope, $rootScope, $routeParams, $resource, $http, $location, $modal, catalogService, configurationService, addonsService, entityMixins) {

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
                            $scope.isLoading = false;
                            if (status < 400) {
                                var fragments = headers("location").split('/'),
                                    slug = fragments[fragments.length - 1];
                                $rootScope.$broadcast('catalog:refreshCatalog');
                                $location.url("/products/" + slug);
                            }
                            else {
                                if (status === 409) {
                                    $modal.open({ templateUrl: 'nameConflictError.html' });
                                }
                                else {
                                    // Generic error
                                    $modal.open({ templateUrl: 'serverError.html' });
                                }
                            }
                        })
                        .error(function (data, status, headers, config) {
                            $modal.open({ templateUrl: 'serverError.html' });
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

            $scope.createVariant = function () {

                if ($scope.productForm.type.$dirty) {
                    // NOTE:
                    // If the product's type is dirty (has not been saved), we might try to create here a variant
                    // for a product with no type defined (from the server-side point of view), which will be refused
                    // (as long as variants aren't supported outside the notion of product type).
                    // What we do here is force a save. This is not ideal since it's hidden to the user, and he could
                    // have edited other things.

                    $scope.updateProduct();
                    if (typeof $scope.product._links.variants === 'undefined') {
                        // The variants links might not exist as well and is going to be used so we create it lazily.
                        $scope.product._links.variants = {
                            href: $scope.product._href + "variants/"
                        }
                    }
                }

                $scope.variantToCreate = {
                    features: {}
                };
                $scope.modalInstance = $modal.open({
                    templateUrl: 'createVariant.html',
                    controller: 'VariantController',
                    scope: $scope
                });
                $scope.modalInstance.result.then(function () {
                    $http.post($scope.product._links.variants.href, $scope.variantToCreate)
                        .success(function (data, status, headers) {
                            if (status < 400) {
                                var fragments = headers("location").split('/'),
                                    slug = fragments[fragments.length - 1];

                                delete $scope.variantToCreate;

                                $scope.reloadVariants(function () {
                                    var variant = $scope.product._embedded.variants.find(function (variant) {
                                        return variant.slug === slug;
                                    });

                                    $scope.editVariant(variant);
                                });
                            }
                            else if (status == 409) {
                                var conflictErrorScope = $scope.$new();
                                conflictErrorScope.errorKey = 'product.variants.conflict';
                                $scope.modalInstance = $modal.open({ templateUrl: 'conflictError.html', scope: conflictErrorScope });
                            }
                            else {
                                // Generic error
                                $scope.modalInstance = $modal.open({ templateUrl: 'serverError.html' });
                            }
                        });
                });
            }

            $scope.deleteVariant = function (variant) {
                $http.delete(variant._href).then(function () {
                    $scope.modalInstance.close();
                    $scope.reloadVariants();
                });
            }

            $scope.confirmDeletionOfVariant = function (variant) {
                $scope.modalInstance = $modal.open({ templateUrl: 'confirmDeletionVariant.html' });
                $scope.modalInstance.result.then(function () {
                    $scope.deleteVariant(variant)
                });
            }

            $scope.reloadVariants = function (callback) {
                $http.get($scope.product._links.variants.href)
                    .success(function (data) {
                        $scope.product._embedded.variants = data;
                        $scope.initializeVariants();

                        callback && callback($scope.product._embedded.variants)
                    });
            }

            $scope.editVariant = function (variant) {
                $scope.variant = variant;

                $scope.modalInstance = $modal.open({
                    templateUrl: 'editVariant.html',
                    scope: $scope
                });

                $scope.modalInstance.result.then(function () {
                    $http.post($scope.variant._href, $scope.variant).success(function (data, status) {
                        $scope.reloadVariants();
                    });

                    delete $scope.variant;
                });
            }

            $scope.collectionOperation = function (collection, operation) {
                $resource("/api/collections/:slug/:operation", {"slug": collection.slug, "operation": operation}, {
                    "save": {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    }
                }).save("product=" + $scope.product.slug, function () {
                        $rootScope.$broadcast('catalog:refreshCatalog');
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
                        angular.forEach($scope.product._relationships.collections, function (productCollection) {
                            if (collection._href == productCollection._href) {
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

            $scope.initializeVariants = function () {
                if ($scope.hasTypes) {
                    var variants = $scope.product._embedded.variants;

                    variants.forEach(function (variant) {

                        if (typeof variant.addons === "undefined") {
                            variant.addons = [];
                        }

                        addonsService.initializeEntityAddons("product", variant, {
                            getDefinition: function (entityDefinition) {
                                return {
                                    // TODO
                                    // See how platform types can be supported and if/how we can get rid of this
                                    // "source" parameter
                                    theme: entityDefinition.types[$scope.product.type].variants.addons
                                };
                            }
                        }).then(function (addons) {
                                if (typeof $scope.variantAddons === "undefined") {
                                    $scope.variantAddons = addons;
                                }
                            });
                    });
                }
            }

            configurationService.get("catalog", function (catalogConfiguration) {
                $scope.hasWeight = catalogConfiguration.products.weight;
                $scope.weightUnit = catalogConfiguration.products.weightUnit;
                $scope.hasStock = catalogConfiguration.products.stock;
                $scope.mainCurrency = catalogConfiguration.currencies.main;
            });

            // We initialize the "hasTypes" flag to true because the flickering it creates (for the time the AJAX
            // call to the configuration is made) is less impactful when we hide the type switch than when we display
            // the variants block a bit late.
            // TODO: we can remove this when we cache the configuration properly (with a server-sent event that flushes
            // caches properly when configuraiton changes)
            $scope.hasTypes = true;

            // Initialize types
            configurationService.get("entities", function (entities) {
                if (typeof entities.product !== 'undefined') {
                    $scope.types = entities.product.types;
                    $scope.hasTypes = !!(Object.keys(angular.copy($scope.types)).length > 0);

                    $scope.initializeVariants();
                }
                else {
                    $scope.types = {};
                    $scope.hasTypes = false;
                }
            });

            $scope.keys = function (object) {
                var keys = [];
                if (typeof object === "undefined") {
                    return keys;
                }
                for (var key in object) {
                    object.hasOwnProperty(key)
                    keys.push(key);
                }
                return keys;
            }

            // Initialize existing product or new product

            if (!$scope.isNew()) {
                $scope.product = $scope.ProductResource.get({ "slug": $scope.slug }, function () {
                    $scope.reloadImages();

                    // Ensures the collection initialization happens after the AJAX callback
                    $scope.initializeCollections();

                    // Same for addons
                    $scope.initializeEntity();

                    if ($scope.product.onShelf == null) {
                        // "null" does not seem to be evaluated properly in angular directives
                        // (like ng-show="something != null")
                        // Thus, we convert "null" onShelf to undefined to be able to have that "high impedance"
                        // state in angular directives.
                        $scope.product.onShelf = undefined;
                    }
                });
            }
            else {
                $scope.product = $scope.newProduct();

                $scope.initializeEntity();
            }

            $scope.confirmDeletion = function () {
                $scope.modalInstance = $modal.open({ templateUrl: 'confirmDeletionProduct.html' });
                $scope.modalInstance.result.then($scope.deleteProduct);
            }

            $scope.deleteProduct = function () {
                $scope.ProductResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $scope.modalInstance.close();
                    $rootScope.$broadcast('catalog:refreshCatalog');
                    $location.url("/catalog");
                });
            }

            $scope.getTranslationProperties = function () {
                return {
                    productTitle: $scope.localizedProduct.title,
                    imagesLength: $scope.getNumberOfImages(),
                    variantTitle: ($scope.variant || {}).title
                };
            };

        }])

    .controller('VariantController', [ '$scope', function ($scope) {
        $scope.getKeys = function (feature) {
            return $scope.types[$scope.product.type].features[feature].keys
        }
    }])

;
