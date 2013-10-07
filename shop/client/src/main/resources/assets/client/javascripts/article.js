'use strict';

angular.module('article', ['ngResource'])

    .controller('ArticleController', [
        '$scope',
        '$rootScope',
        '$routeParams',
        '$resource',
        '$http',
        '$location',
        'addonsService',
        'imageService',
        'timeService',
        'configurationService',

        function ($scope, $rootScope, $routeParams, $resource, $http, $location, addonsService, imageService, timeService, configurationService) {

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

            $scope.slug = $routeParams.article;

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
                                    $rootScope.$broadcast('event:nameConflictError');
                                }
                                else {
                                    // Generic error
                                    $rootScope.$broadcast('event:serverError');
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

            $scope.editThumbnails = function (image) {
                $rootScope.$broadcast('thumbnails:edit', "article", image);
            };

            $scope.ArticleResource = $resource("/api/news/:slug");

            $scope.isNew = function () {
                return $scope.slug == "_new";
            };

            $scope.newArticle = function () {
                return {
                    slug: "",
                    title: "",
                    addons: []
                };
            };

            $scope.reloadImages = function () {
                $scope.article.images = $http.get("/api/news/" + $scope.slug + "/images").success(function (data) {
                    $scope.article.images = data;
                });
            };

            $scope.selectFeatureImage = function (image) {
                imageService.selectFeatured($scope.article, image);
            };

            $scope.removeImage = function(image) {
                $http.delete("/api/news/" + $scope.slug + "/images/" + image.slug).success(function () {
                    $scope.reloadImages();
                });
            };

            $scope.getImageUploadUri = function () {
                return "/api/news/" + $scope.slug + "/attachments";
            };

            $scope.initializeAddons = function () {
                addonsService.initializeEntityAddons("article", $scope.article).then(function (addons) {
                    $scope.addons = addons;
                });
            };


            $scope.initializeModels = function () {
                $scope.models = [];
                configurationService.get("entities", function (entities) {
                    if (typeof entities.article !== 'undefined') {
                        for (var modelId in entities.article.models) {
                            if (entities.article.models.hasOwnProperty(modelId)) {
                                var model = entities.article.models[modelId];
                                $scope.models.push({
                                    id: modelId,
                                    name: model.name
                                });
                            }
                        }
                    }
                });
            };

            // Initialize existing page or new page

            $scope.getArticle = function() {
                $scope.article = $scope.ArticleResource.get({
                    "slug": $scope.slug,
                    "expand": ["images"] }, function () {

                    $scope.initializeAddons();
                    $scope.initializeModels();

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
                $scope.initializeAddons();
                $scope.initializeModels();
            }

            $scope.confirmDeletion = function () {
                $rootScope.$broadcast('article:confirmDelete');
            };

            $scope.deleteArticle = function () {
                $scope.ArticleResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $rootScope.$broadcast('article:dismissConfirmDelete');
                    $rootScope.$broadcast("news:articles:refreshList");
                    $location.url("/contents");
                });
            };

            $scope.getTranslationProperties = function () {
                var article = $scope.article || {};

                return {
                    articleDate: timeService.convertISO8601toLocalDate(article.publicationDate || '', 'LLL'),
                    imagesLength: (article.images || {}).length || 0
                };
            };

        }]);
