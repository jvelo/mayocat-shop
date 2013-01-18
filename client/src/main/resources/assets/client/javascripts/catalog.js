'use strict'

angular.module('catalog', [])
    .service('catalogService', function ($http, configurationService) {
        return {
            listProducts:function (callback) {
                $http.get('/product/').success(function (data) {
                    callback && callback.call(this, data);
                });
            },
            listCategories:function (callback) {
                $http.get('/category/').success(function (data) {
                    callback && callback.call(this, data);
                });
            },
            moveCategory:function (slug, targeg, position) {
            },
            moveProduct:function (slug, target, position) {
                $http.post('/category/_all/move',
                    "product=" + slug + "&" + position + "=" + target,
                    { "headers":{'Content-Type':'application/x-www-form-urlencoded'} })
                    .success(function (data) {
                    });
            },
            move:function (path, slug, target, position) {
                $http.post(path + slug + "/move",
                    position + "=" + target,
                    { "headers":{'Content-Type':'application/x-www-form-urlencoded'} })
                    .success(function (data) {
                    })
                    .error(function (data, status) {

                    });
            }

        };
    })
    .controller('CatalogController', ['$scope', '$location', 'catalogService',
    function ($scope, $location, catalogService) {

        // List of products
        $scope.products = [];

        // A "move position" operation to perform. It is set by the 'sortable' directive when the list sort order changes.
        $scope.changeOperation = undefined;

        $scope.setRoute = function (href) {
            $location.url(href);
        };

        catalogService.listProducts(function (products) {
            $scope.products = products;
        });

        catalogService.listCategories(function (categories) {
            $scope.categories = categories;
        });

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
        };
    }]);
