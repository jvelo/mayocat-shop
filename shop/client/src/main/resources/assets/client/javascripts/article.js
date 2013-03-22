'use strict'

angular.module('article', ['ngResource'])

    .controller('ArticleController', [
    '$scope',
    '$rootScope',
    '$routeParams',
    '$resource',
    '$http',
    '$location',
    'addonsService',
    'configurationService',

    function ($scope, $rootScope, $routeParams, $resource, $http, $location, addonsService, configurationService) {

        $scope.slug = $routeParams.article;

        $scope.publishArticle = function () {
            $scope.article.published = true;
            $scope.updateArticle();
        }

        $scope.updateArticle = function () {
            if ($scope.isNew()) {
                $http.post("/api/1.0/news/", $scope.article)
                    .success(function (data, status, headers, config) {
                        var fragments = headers("location").split('/'),
                            slug = fragments[fragments.length - 1];
                        $location.url("/news/" + slug);
                    })
                    .error(function (data, status, headers, config) {
                        // TODO handle 409 conflict
                    });
            }
            else {
                $scope.ArticleResource.save({ "slug": $scope.slug }, $scope.article);
            }
        };

        $scope.editThumbnails = function (image) {
            $rootScope.$broadcast('thumbnails:edit', image);
        }

        $scope.ArticleResource = $resource("/api/1.0/news/:slug");

        $scope.isNew = function () {
            return $scope.slug == "_new";
        };

        $scope.newArticle = function () {
            return {
                slug: "",
                title: "",
                addons: []
            };
        }

        $scope.reloadImages = function () {
            $scope.article.images = $http.get("/api/1.0/news/" + $scope.slug + "/image").success(function (data) {
                $scope.article.images = data;
            });
        }

        $scope.getImageUploadUri = function () {
            return "/api/1.0/news/" + $scope.slug + "/attachment";
        }

        $scope.initializeAddons = function () {
            addonsService.initialize("article", $scope.page).then(function (addons) {
                $scope.addons = addons;
            });
        }

        $scope.initializeModels = function() {
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
        }

        // Initialize existing page or new page

        if (!$scope.isNew()) {
            $scope.article = $scope.ArticleResource.get({
                "slug": $scope.slug,
                "expand": ["images"] }, function () {

                $scope.initializeAddons();
                $scope.initializeModels();

                if ($scope.article.published == null) {
                    // "null" does not seem to be evaluated properly in angular directives
                    // (like ng-show="something != null")
                    // Thus, we convert "null" published flag to undefined to be able to have that "high impedance"
                    // state in angular directives.
                    $scope.article.published = undefined;
                }
            });
        }
        else {
            $scope.article = $scope.newArticle();
            $scope.initializeAddons();
            $scope.initializeModels();
        }

    }]);
