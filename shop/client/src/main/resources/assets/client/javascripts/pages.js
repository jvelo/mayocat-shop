/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict'

angular.module('pages', [])

    .controller('PagesController', ['$scope', '$resource',
        function ($scope, $resource) {

            $scope.refreshPages = function () {
                $resource("/api/pages").get({}, function (data) {
                    $scope.pages = data.pages;
                });
            }

            $scope.$on("pages:refreshList", function () {
                $scope.refreshPages();
            });

            $scope.refreshPages();

        }]);
