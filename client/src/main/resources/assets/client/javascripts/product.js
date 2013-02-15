'use strict'

angular.module('product', ['ngResource'])

    .controller('ThumbnailsEditorController', ['$scope', '$resource', 'configurationService',
    function ($scope, $resource, configurationService) {

        function reduce(numerator, denominator) {
            if (isNaN(numerator) || isNaN(denominator)) return NaN;
            var gcd = function gcd(a, b) {
                return b ? gcd(b, a % b) : a;
            };
            gcd = gcd(numerator, denominator);
            return (numerator / gcd) + ":" + (denominator / gcd);
        }

        $scope.currentSource = null;

        $scope.edit = function(source, size) {
            $scope.currentSource = source;
            $scope.currentSize = size;
        }

        $scope.next = function () {
            var nextSource = false;
            for (var source in $scope.configuration) {
                if ($scope.configuration.hasOwnProperty(source)) {
                    if (nextSource && $scope.configuration[source].length > 0) {
                        $scope.currentSource = source;
                        $scope.currentSize = $scope.configuration[source][0];
                        console.log($scope.currentSource, $scope.currentSize);
                    }
                    for (var i = 0; i < $scope.configuration[source].length; i++) {
                        if (source === $scope.currentSource &&
                            $scope.configuration[source][i].name === $scope.currentSize.name) {
                            if (typeof $scope.configuration[source][i + 1] !== 'undefined') {
                                $scope.currentSize = $scope.configuration[source][i + 1];
                                return;
                            }
                            else {
                                nextSource = true;
                            }
                        }
                    }
                }
            }
        }

        $scope.getSelection = function(source, size) {
            var thumbnail = $scope.find(source, size) || $scope.findSameRatio(source, size);
            if (typeof thumbnail !== 'undefined') {
                return [thumbnail.x, thumbnail.y, thumbnail.x + thumbnail.width, thumbnail.y + thumbnail.height];
            }
            else {
                // Compute max bounding size
            }
        }

        $scope.isEdited = function(source, size) {
            if ($scope.currentSource === source && $scope.currentSize.name === size.name) {
                return true;
            }
            return false;
        }

        $scope.getType = function(source, size) {
            if (typeof $scope.image !== 'undefined') {
                if (typeof $scope.find(source, size) !== "undefined") {
                    return "icon-ok";
                }
                else if (typeof $scope.findSameRatio(source, size) !== "undefined") {
                    return "icon-resize-horizontal";
                }
            }
            return "icon-remove";
        }

        $scope.find = function(source, size) {
            var found;
            angular.forEach($scope.image.thumbnails, function (thumbnail) {
                var ratioEquals = thumbnail.ratio === reduce(size.dimensions.width, size.dimensions.height);
                if (source == thumbnail.source && size.name == thumbnail.hint && ratioEquals) {
                    found = thumbnail;
                }
            });
            return found;
        }

        $scope.findSameRatio = function(source, size) {
            var found;
            angular.forEach($scope.image.thumbnails, function (thumbnail) {
                var ratioEquals = thumbnail.ratio === reduce(size.dimensions.width, size.dimensions.height);
                if (ratioEquals) {
                    found = thumbnail;
                }
            });
            return found;
        }

        $scope.$on('thumbnails:edit:selection', function(event, coordinates) {
            $scope.currentCoordinates = {
                "x" : Math.round(coordinates.x),
                "y" : Math.round(coordinates.y),
                "width" :  Math.round(coordinates.w),
                "height" :  Math.round(coordinates.h)
            };
        });

        $scope.$on('thumbnails:edit', function(event, image) {
            $scope.image = image;
            $scope.$broadcast('thumbnails:edit:ready', {
                'image': image
            });
        });

        $scope.addOrUpdateThumbnail = function (data) {
           for (var i=0; i<$scope.image.thumbnails.length; i++) {
               if ($scope.image.thumbnails[i].source === data.source
                   && $scope.image.thumbnails[i].hint === data.hint) {
                   $scope.image.thumbnails[i] = data;
                   return;
               }
           }
            $scope.image.thumbnails.push(data);
        }

        $scope.save = function () {
            if (typeof $scope.currentCoordinates !== "undefined") {
                var data = {
                    "hint":$scope.currentSize.name,
                    "source":$scope.currentSource,
                    "x":$scope.currentCoordinates.x,
                    "y":$scope.currentCoordinates.y,
                    "width":$scope.currentCoordinates.width,
                    "height":$scope.currentCoordinates.height,
                    "ratio":reduce($scope.currentSize.dimensions.width, $scope.currentSize.dimensions.height)
                };
                $resource("/api/1.0" + $scope.image.href + "/thumbnail/", {}, {
                    "save":{
                        "method":"PUT"
                    }
                }).save(data, function () {
                        $scope.addOrUpdateThumbnail(data);
                        $scope.next();
                    });
            }
        }

        configurationService.get(function(data){
           $scope.configuration = data.thumbnails;
           $scope.currentSource = "platform";
           $scope.currentSize = $scope.configuration.platform[0];
        });

    }])

    .controller('ProductController', ['$scope', '$rootScope', '$routeParams', '$resource', '$location', 'catalogService',
    function ($scope, $rootScope, $routeParams, $resource, $location, catalogService) {

        $scope.slug = $routeParams.product;

        $scope.updateProduct = function () {
            if ($scope.isNew()) {
                $resource("/api/1.0/product/").save($scope.product, function (response) {
                    $location.path(response.href);
                });
            }
            else {
                $scope.ProductResource.save({ "slug":$scope.slug }, $scope.product);
                angular.forEach($scope.categories, function (category) {
                    if (category.hasProduct && !category.hadProduct) {
                        $scope.categoryOperation(category, "addProduct");
                    }
                    if (category.hadProduct && !category.hasProduct) {
                        $scope.categoryOperation(category, "removeProduct");
                    }
                });
            }
        };

        $scope.editThumbnails = function(image) {
            $rootScope.$broadcast('thumbnails:edit', image);
        }

        $scope.categoryOperation = function (category, operation) {
            $resource("/api/1.0/category/:slug/:operation", {"slug":category.slug, "operation" : operation}, {
                "save":{
                    method:'POST',
                    headers:{
                        'Content-Type':'application/x-www-form-urlencoded'
                    }
                }
            }).save("product=" + $scope.product.slug, function () { });
        };

        $scope.ProductResource = $resource("/api/1.0/product/:slug");

        $scope.isNew = function () {
            return $scope.slug == "_new";
        };

        $scope.newProduct = function () {
            return {
                slug:"",
                title:""
            };
        }

        $scope.reloadImages = function() {
            $scope.product.images = $resource("/api/1.0/product/:slug/image").get({
                "slug": $scope.slug
            });
        }

        $scope.getImageUploadUri = function() {
            return "/api/1.0/product/" + $scope.slug + "/attachment";
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

        if (!$scope.isNew()) {
            $scope.product = $scope.ProductResource.get({
                "slug": $scope.slug,
                "expand": ["categories", "images"] }, function () {
                // Ensures the category initialization happens after the AJAX callback
                $scope.initializeCategories();
            });
        }
        else {
            $scope.product = $scope.newProduct();
        }

    }]);
