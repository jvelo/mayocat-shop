'use strict'

angular.module('catalog', [])
    .service('catalogService', function ($http, configurationService) {
        return {
            hasCategories: function(callback) {
                configurationService.get("shop.products.categories", function(hasCategories){
                    callback && callback.call(this, hasCategories);
                });
            },

            listProducts:function (callback) {
                this.hasCategories(function(hasCategories){
                    if (!hasCategories) {
                        $http.get('/api/1.0/product/').success(function (data) {
                            callback && callback.call(this, data);
                        });
                    }
                    else {
                        $http.get('/api/1.0/product/?filter=uncategorized').success(function (data) {
                            callback && callback.call(this, data);
                        });
                    }
                });
            },

            listProductsForCategory:function (category, callback) {
                $http.get('/api/1.0/category/' + category + "?expand=products").success(function (data) {
                    callback && callback.call(this, data.products);
                });
            },

            listCategories:function (callback) {
                this.hasCategories(function(hasCategories){
                    if (!hasCategories) {
                        callback && callback.call(this, []);
                    }
                    $http.get('/api/1.0/category/?expand=productCount').success(function (data) {
                        callback && callback.call(this, data);
                    });
                });
            },
            moveCategory:function (slug, target, position) {
            },
            moveProduct:function (slug, target, position) {
                $http.post('/api/1.0/category/_all/move',
                    "product=" + slug + "&" + position + "=" + target,
                    { "headers":{'Content-Type':'application/x-www-form-urlencoded'} })
                    .success(function (data) {
                    });
            },
            move:function (path, slug, target, position) {
                $http.post("/api/1.0" + path + slug + "/move",
                    position + "=" + target,
                    { "headers":{'Content-Type':'application/x-www-form-urlencoded'} })
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

        catalogService.hasCategories(function (has) {
            $scope.hasCategories = has;
        });

        catalogService.listProducts(function (products) {
            $scope.products = products;
        });

        catalogService.listCategories(function (categories) {
            $scope.categories = categories;
        });

        $scope.toggleExpand = function(category) {
            if (typeof category.products === "undefined") {
                catalogService.listProductsForCategory(category.slug, function(products){
                    category.products = products;
                    category.isExpanded = true;
                    console.log(category.products);
                });
            }
            else {
                category.isExpanded = !category.isExpanded;
            }
        }

        $scope.changePosition = function () {
            if (typeof $scope.changeOperation === "undefined") {
                return;
            }

            catalogService.move(
                $location.path(),
                $scope.changeOperation.handle,
                $scope.changeOperation.target,
                $scope.changeOperation.position
            );

            $scope.changeOperation = undefined;
        }

        configurationService.get("shop.products.categories", function(value){
          $scope.hasCategories = value;
        });
    }]);
