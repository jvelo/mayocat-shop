angular.module('thumbnail', ['ngResource'])

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
            return undefined;
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

    }]);