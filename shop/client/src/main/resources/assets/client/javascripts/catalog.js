'use strict'

angular.module('catalog', [])
    .service('catalogService', function ($http, configurationService) {
        return {
            hasCollections: function (callback) {
                configurationService.get("catalog.products.collections", function (hasCollections) {
                    callback && callback.call(this, hasCollections);
                });
            },

            listProducts: function (callback) {
                this.hasCollections(function (hasCollections) {
                    if (!hasCollections) {
                        $http.get('/api/products/').success(function (data) {
                            callback && callback.call(this, data);
                        });
                    }
                    else {
                        $http.get('/api/products/?filter=uncategorized').success(function (data) {
                            callback && callback.call(this, data);
                        });
                    }
                });
            },

            listProductsForCollection: function (collection, callback) {
                $http.get('/api/collections/' + collection + "?expand=products").success(function (data) {
                    callback && callback.call(this, data.products);
                });
            },

            listCollections: function (callback) {
                this.hasCollections(function (hasCollections) {
                    if (!hasCollections) {
                        callback && callback.call(this, []);
                    }
                    $http.get('/api/collections/?expand=productCount').success(function (data) {
                        callback && callback.call(this, data);
                    });
                });
            },
            moveProduct: function (path, slug, target, position) {
                $http.post('/api/collections/' + path + '/moveProduct',
                    "product=" + slug + "&" + position + "=" + target,
                    { "headers": {'Content-Type': 'application/x-www-form-urlencoded'} })
                    .success(function (data) {
                    });
            },
            move: function (path, slug, target, position) {
                $http.post("/api/" + path + "/"  + slug + "/move",
                    position + "=" + target,
                    { "headers": {'Content-Type': 'application/x-www-form-urlencoded'} })
                    .success(function (data) {
                    })
                    .error(function (data, status) {

                    });
            }

        };
    })
    .controller('CatalogController', ['$scope', '$location', 'catalogService', 'configurationService',
        function ($scope, $location, catalogService, configurationService) {

            // List of products
            $scope.products = [];

            // A "move position" operation to perform. It is set by the 'sortable' directive when the list sort order changes.
            $scope.changeOperation = undefined;

            $scope.setRoute = function (href) {
                $location.url(href);
            };

            $scope.refreshCatalog = function () {
                catalogService.hasCollections(function (has) {
                    $scope.hasCollections = has;
                });

                catalogService.listProducts(function (products) {
                    $scope.products = products;
                });

                catalogService.listCollections(function (collections) {
                    $scope.collections = collections;
                    angular.forEach($scope.collections, function (collection) {
                        $scope.toggleExpand(collection);
                    });
                });
            }

            $scope.toggleExpand = function (collection) {
                if (typeof collection.products === "undefined") {
                    catalogService.listProductsForCollection(collection.slug, function (products) {
                        collection.products = products;
                        collection.isExpanded = true;
                    });
                }
                else {
                    collection.isExpanded = !collection.isExpanded;
                }
            }

            $scope.collectionSortableOptions = {
                update: function(event, ui) {
                    var itemScope = ui.item.scope(),
                        index = ui.item.index(),
                        target,
                        afterOrBefore;

                    if (index > 0) {
                        target = ui.item.prev().scope().collection.slug;
                        afterOrBefore = "after";
                    }
                    else {
                        target = ui.item.next().scope().collection.slug;
                        afterOrBefore = "before";
                    }

                    catalogService.move(
                        "collections",
                        itemScope.collection.slug,
                        target,
                        afterOrBefore
                    );
                }
            }


            $scope.productsInCollectionSortableOptions = {
                update: function(event, ui) {
                    var itemScope = ui.item.scope(),
                        index = ui.item.index(),
                        target,
                        afterOrBefore;

                    if (index > 0) {
                        target = ui.item.prev().scope().product.slug;
                        afterOrBefore = "after";
                    }
                    else {
                        target = ui.item.next().scope().product.slug;
                        afterOrBefore = "before";
                    }

                    var collectionSlug = itemScope.$parent.collection.slug;

                    catalogService.moveProduct(
                        collectionSlug,
                        itemScope.product.slug,
                        target,
                        afterOrBefore
                    );
                }
            }

            $scope.uncategorizedProductsSortableOptions = {
                update: function(event, ui) {
                    var itemScope = ui.item.scope(),
                        index = ui.item.index(),
                        target,
                        afterOrBefore;

                    if (index > 0) {
                        target = ui.item.prev().scope().product.slug;
                        afterOrBefore = "after";
                    }
                    else {
                        target = ui.item.next().scope().product.slug;
                        afterOrBefore = "before";
                    }

                    catalogService.move(
                        "products",
                        itemScope.product.slug,
                        target,
                        afterOrBefore
                    );
                }
            }

            configurationService.get("catalog.products.collections", function (value) {
                $scope.hasCollections = value;
            });

            $scope.refreshCatalog();

            $scope.$on("catalog:refreshCatalog", function () {
                $scope.refreshCatalog();
            })

            $scope.getTranslationProperties = function () {
                return {
                    numberOfProducts: $scope.products.length
                };
            };

        }]);
