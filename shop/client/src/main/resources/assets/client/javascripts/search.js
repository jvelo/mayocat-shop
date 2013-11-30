/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('search', [])
    .service('searchService', function ($http) {
        return {
            search: function (term, callback) {
                $http.get('/api/search/?term=' + term).success(function (data) {
                    var result = [];
                    for (var i = 0; i < data.length; i++) {
                        result.push(data[i]);
                    }
                    callback && callback.call(this, result);
                });
            }
        };
    })
    .controller('SearchController', ['$scope', '$location', 'searchService',
        function ($scope, $location, searchService) {
            $scope.term = "";
            $scope.suggestions = [];
            $scope.clear = function () {
                $scope.term = "";
                $scope.suggestions = [];
            }
            $scope.setRoute = function (slug) {
                // FIXME should get route from entity
                $location.url("/products/" + slug);
                $scope.clear();
            }
            $scope.$watch('term', function (term) {
                if (term != "") {
                    searchService.search(term, function (result) {
                        $scope.suggestions = result;
                    });
                }
            });
        }]);

