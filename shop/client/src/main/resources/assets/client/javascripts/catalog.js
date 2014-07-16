/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('catalog', [])
    .service('catalogService',['$http', '$translate', 'configurationService', 'notificationService',
        function($http, $translate, configurationService, notificationService) {
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
                                callback && callback.call(this, data.products);
                            });
                        }
                    });
                },

                listProductsForCollection: function (collection, callback) {
                    $http.get('/api/collections/' + collection + "?expand=products").success(function (data) {
                        callback && callback.call(this, data._relationships.products);
                    });
                },

                listCollections: function (callback) {
                    this.hasCollections(function (hasCollections) {
                        if (!hasCollections) {
                            callback && callback.call(this, []);
                        }
                        $http.get('/api/collections/?expand=productCount').success(function (data) {
                            callback && callback.call(this, data.collections);
                        });
                    });
                },
                moveProduct: function (path, slug, target, position) {
                    $http.post('/api/collections/' + path + '/moveProduct',
                        "product=" + slug + "&" + position + "=" + target,
                        { "headers": {'Content-Type': 'application/x-www-form-urlencoded'} })
                        .success(function (data) {
                            notificationService.notify($translate('product.status.moved'));
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
        }])
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
                });
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

        }])
    
    // Manages the expanded status of a collection and some visual behaviours.
    .directive('catalogCollection', ['catalogService', function(catalogService) {

        function setStatus(slug, isExpanded) {
            var storage = JSON.parse(localStorage['collectionsStatus'] || '{}');
            storage[slug] = isExpanded;
            localStorage['collectionsStatus'] = JSON.stringify(storage);
            return isExpanded;
        }

        function getStatus(slug) {
            var storage = JSON.parse(localStorage['collectionsStatus'] || '{}');
            return (typeof storage[slug] != 'undefined') ? storage[slug] : true;
        }

        function link(scope, element) {
            var collection = scope.collection;

            collection.isExpanded = getStatus(collection.slug);

            scope.toggleExpand = function(applyInsteadOfToggle) {
                var emptyProducts = (typeof collection.products == 'undefined');

                if (applyInsteadOfToggle) {
                    // If we just want to apply the value (ie. load the products if necessary), just inverse the value
                    // and it'll be we inversed again just below.
                    collection.isExpanded = !collection.isExpanded;
                }

                if (!emptyProducts || emptyProducts && collection.isExpanded) {
                    collection.isExpanded = setStatus(collection.slug, !collection.isExpanded);
                } else {
                    catalogService.listProductsForCollection(collection.slug, function(products) {
                        collection.products = products;
                        collection.isExpanded = setStatus(collection.slug, true);
                    });
                }
            };

            // When the collection is added to the DOM, execute the toggleExpand()
            // method to load the products if necessary.
            scope.toggleExpand(true);

            // Solves some visual behaviours which can't be done in CSS.
            var $collection = $(element);

            $collection.find('> div > ul.list')
                .on('mouseover mouseout', function(event) {
                    $collection.toggleClass('disable-hover', event.type == 'mouseover');
                });
        }

        return {
            restrict: 'A',
            link: link
        };
    }]);
