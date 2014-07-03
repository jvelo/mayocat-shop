/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
'use strict';

angular.module('article', ['ngResource'])

    .controller('ArticleController', [
        '$scope',
        '$rootScope',
        '$routeParams',
        '$resource',
        '$http',
        '$location',
        '$modal',
        'timeService',
        'configurationService',
        'entityMixins',

        function ($scope, $rootScope, $routeParams, $resource, $http, $location, $modal, timeService, configurationService, entityMixins) {

            entityMixins.extend(["base", "image", "addons"], $scope, "article", {
                "base" : {
                    "apiBase" : "/api/news/"
                }
            });

            /**
             * Helper function to parse time entered by a user.
             *
             * @param input the user entered input
             * @return {Boolean|String} false if no sensible time could be parse, the formatted time as HH:MM otherwise
             */
            var parseUserEnteredTime = function (input) {
                var result = false, matches;
                var re = /^\s*([01]?\d|2[0-3]):?([0-5]\d)\s*$/;
                if ((matches = input.match(re))) {
                    result = (matches[1].length == 2 ? "" : "0") + matches[1] + ":" + matches[2];
                }
                return result;
            };

            $scope.publishArticle = function () {
                $scope.article.published = true;
                $scope.updateArticle(function(){
                    $scope.getArticle();
                });
            };

            $scope.updateArticle = function (callback) {
                $scope.isSaving = true;
                if ($scope.isNew()) {
                    $http.post("/api/news/", $scope.article)
                        .success(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            if (status < 400) {
                                var fragments = headers("location").split('/'),
                                    slug = fragments[fragments.length - 1];
                                $location.url("/news/" + slug);
                                $rootScope.$broadcast("news:articles:refreshList");
                                callback && callback.call();
                            }
                            else {
                                if (status === 409) {
                                    $modal.open({ templateUrl: 'conflictError.html' });
                                }
                                else {
                                    // Generic error
                                    $modal.open({ templateUrl: 'serverError.html' });
                                }
                            }
                        })
                        .error(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            callback && callback.call();
                        });
                }
                else {
                    $scope.ArticleResource.save({ "slug": $scope.slug }, $scope.article, function () {
                        $scope.isSaving = false;
                        $rootScope.$broadcast("news:articles:refreshList");
                        callback && callback.call();
                    });
                }
            };

            $scope.changePublicationDate = function () {
                $scope.newPublicationDate = timeService.convertISO8601toLocalDate($scope.article.publicationDate, "YYYY-MM-DD");
                $scope.newPublicationTime = timeService.convertISO8601toLocalDate($scope.article.publicationDate, "HH:mm");
            };

            $scope.validateNewPublicationDate = function () {
                var newTime = parseUserEnteredTime($scope.newPublicationTime);
                if (!newTime) {
                    // old is the new new
                    newTime = timeService.convertISO8601toLocalDate($scope.article.publicationDate, "HH:mm");
                }
                // construct a floating iso8601 date (without tz)
                var newDate = $scope.newPublicationDate + "T" + newTime;

                $scope.article.publicationDate = newDate;
                $scope.cancelChangePublicationDate();
            };

            $scope.cancelChangePublicationDate = function () {
                $scope.newPublicationDate = null;
                $scope.newPublicationTime = null;
            };



            $scope.ArticleResource = $resource("/api/news/:slug");

            // Initialize existing page or new page

            $scope.getArticle = function() {
                $scope.article = $scope.ArticleResource.get({
                    "slug": $scope.slug,
                    "expand": ["images"] }, function () {

                    $scope.initializeEntity();

                    if ($scope.article.published === null) {
                        // "null" does not seem to be evaluated properly in angular directives
                        // (like ng-show="something != null")
                        // Thus, we convert "null" published flag to undefined to be able to have that "high impedance"
                        // state in angular directives.
                        $scope.article.published = undefined;
                    }
                });
            };

            if (!$scope.isNew()) {
                $scope.getArticle();
            }
            else {
                $scope.article = $scope.newArticle();
                $scope.initializeEntity();
            }

            $scope.confirmDeletion = function () {
                $scope.modalInstance = $modal.open({ templateUrl: 'confirmDeletionArticle.html' });
                $scope.modalInstance.result.then($scope.deleteArticle);
            };

            $scope.deleteArticle = function () {
                $scope.ArticleResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $scope.modalInstance.close();
                    $rootScope.$broadcast("news:articles:refreshList");
                    $location.url("/news");
                });
            };

            $scope.getTranslationProperties = function () {
                var article = $scope.article || {};

                return {
                    articleTitle: $scope.article.title,
                    articleDate: timeService.convertISO8601toLocalDate(article.publicationDate || '', 'LLL'),
                    imagesLength: $scope.getNumberOfImages()
                };
            };

        }]);
