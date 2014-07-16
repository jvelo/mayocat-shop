/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
angular.module('mayocat.image', ['ngResource'])

    .controller('ImageEditorController', ['$scope', '$rootScope', '$resource', '$http', '$modal', '$translate', 'configurationService', 'entityMixins',
        function ($scope, $rootScope, $resource, $http, $modal, $translate, configurationService, entityMixins) {

            entityMixins.extend("localization", $scope, "image");

            function initializeEdition() {

                var themeSource = [],
                    currentSize;

                if (typeof $scope.configuration[$scope.entityType] !== 'undefined'
                 && typeof $scope.configuration[$scope.entityType].images !== 'undefined') {
                    themeSource = $scope.configuration[$scope.entityType].images.theme;
                }

                for (var key in themeSource) {
                    themeSource[key].id = key;
                    if (themeSource.hasOwnProperty(key)) {
                        // First one when evaluating <li ng-repeat="size in configuration[entityType].thumbnails[source]"
                        // in editImage.html is the last one here - angular.js likely does not evaluate with for var in
                        currentSize = themeSource[key];
                    }
                }
                $scope.currentSize = currentSize;
                $scope.initializeLocalization();
            }

            function reduce(numerator, denominator) {
                if (isNaN(numerator) || isNaN(denominator)) return NaN;
                var gcd = function gcd(a, b) {
                    return b ? gcd(b, a % b) : a;
                };
                gcd = gcd(numerator, denominator);
                return (numerator / gcd) + ":" + (denominator / gcd);
            }

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

            $scope.edit = function (source, size) {
                $scope.currentSource = source;
                $scope.currentSize = size;
            }

            $scope.getSelection = function (source, size) {
                var thumbnail = $scope.find(source, size) || $scope.findSameRatio(source, size);
                if (typeof thumbnail !== 'undefined') {
                    return [thumbnail.x, thumbnail.y, thumbnail.x + thumbnail.width, thumbnail.y + thumbnail.height];
                }
                return undefined;
            }

            $scope.isEdited = function (source, size) {
                if ($scope.currentSource === source && $scope.currentSize.id === size.id) {
                    return true;
                }
                return false;
            }

            $scope.getType = function (source, size) {
                if (typeof $scope.image !== 'undefined') {
                    if (typeof $scope.find(source, size) !== "undefined") {
                        return "icon-ok";
                    }
                    else if (typeof $scope.findSameRatio(source, size) !== "undefined") {
                        return "icon-resize-horizontal";
                    }
                }
                return;
            }

            $scope.find = function (source, size) {
                var found;
                angular.forEach($scope.image.thumbnails, function (thumbnail) {
                    var ratioEquals = thumbnail.ratio === reduce(size.width, size.height);
                    if (source == thumbnail.source && size.name == thumbnail.hint && ratioEquals) {
                        found = thumbnail;
                    }
                });
                return found;
            }

            $scope.findSameRatio = function (source, size) {
                var found;
                angular.forEach($scope.image.thumbnails, function (thumbnail) {
                    var ratioEquals = thumbnail.ratio === reduce(size.width, size.height);
                    if (ratioEquals) {
                        found = thumbnail;
                    }
                });
                return found;
            }

            $scope.getDisplayRatio = function(size) {
                if (size.width == null) {
                    return size.height + 'px ' + $translate('imageEditor.misc.height');
                } else if (size.height == null) {
                    return size.width + 'px ' + $translate('imageEditor.misc.width');
                } else {
                    return size.width + 'px • ' + size.height + 'px';
                }
            }

            $scope.$on('thumbnails:edit:selection', function (event, coordinates) {
                var x = Math.floor(coordinates.x),
                    y = Math.floor(coordinates.y),
                    width = Math.round(coordinates.w),
                    height = Math.round(coordinates.h);
                $scope.data[$scope.currentSource][$scope.currentSize.id] = {
                    "x": x,
                    "y": y,
                    "width": width,
                    "height": height,
                    "ratio": reduce($scope.currentSize.width, $scope.currentSize.height),
                    "hint": $scope.currentSize.id,
                    "source": $scope.currentSource
                };
            });

            $scope.addOrUpdateThumbnail = function (data) {
                $scope.image.thumbnails = data;
            }

            $scope.save = function() {
                $scope.saving = ["thumbnails", "meta"];
                $scope.saveThumbnails();
                $scope.updateImageMeta();
            }

            $scope.hasSaved = function(what) {
                $scope.saving = $scope.saving.filter(function (i) {
                    return i !== what;
                });
                if ($scope.saving.length === 0) {
                    // Everything has been saved, we close the modal
                    $scope.$close();
                }
            }

            $scope.saveThumbnails = function () {
                var data = [];
                Object.keys($scope.data).forEach(function (source) {
                    Object.keys($scope.data[source]).forEach(function (size) {
                        data.push($scope.data[source][size]);
                    });
                });
                if (data.length > 0) {
                    $http.put($scope.image._href + "/thumbnails/", data)
                        .success(function(data, status) {

                            if (status < 400) {
                                // OK
                                $scope.addOrUpdateThumbnail(data);
                                $scope.hasSaved("thumbnails");
                            } else {
                                // Generic error
                                $modal.open({ templateUrl: 'serverError.html' });
                            }
                        })
                        .error(function() {
                            // Generic error
                            $modal.open({ templateUrl: 'serverError.html' });
                        });
                }
            }

            $scope.updateImageMeta = function () {
                $http.post($rootScope.entity.uri +  "/images/" + $scope.image.slug, $scope.image)
                    .success(function(data, status) {

                        if(status < 400) {
                            // OK
                            $scope.hasSaved("meta");
                        } else {
                            // Generic error
                            $modal.open({ templateUrl: 'serverError.html' });
                        }
                    })
                    .error(function() {
                        // Generic error
                        $modal.open({ templateUrl: 'serverError.html' });
                    });
            }

            configurationService.get(function (data) {
                $scope.configuration = data.entities;
                $scope.currentSource = "theme";
                $scope.data = {};
                ["theme", "platform"].forEach(function (source) {
                    $scope.data[source] = {};
                });

                initializeEdition();
            });

        }])

    .directive('imageList', [function() {
        return {
            restrict: 'E',
            scope: {
                entity: '=',
                sortableOptions: '&',
                selectFeature: '&onSelectFeature',
                edit: '&onEdit',
                remove: '&onRemove'
            },
            templateUrl: '/common/partials/imageList.html'
        };
    }]);