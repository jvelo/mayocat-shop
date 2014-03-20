/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('articles', [])

    .controller('ArticlesController', ['$scope', '$resource',
        function ($scope, $resource) {

            $scope.refreshArticles = function () {
                $resource("/api/news").get({}, function (data) {
                    $scope.articles = data.articles;
                });
            }

            $scope.$on("news:articles:refreshList", function () {
                $scope.refreshArticles();
            });

            $scope.refreshArticles();

        }]);
