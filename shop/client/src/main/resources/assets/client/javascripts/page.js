'use strict'

angular.module('page', ['ngResource'])

    .controller('PageController', [
        '$scope', '$rootScope', '$routeParams', '$resource', '$http', '$location',
        function ($scope, $rootScope, $routeParams, $resource, $http, $location) {

            $scope.slug = $routeParams.page;

            $scope.publishPage = function () {
                $scope.page.published = true;
                $scope.updatePage();
            }

            $scope.updatePage = function () {
                if ($scope.isNew()) {
                    $http.post("/api/1.0/page/", $scope.page)
                        .success(function (data, status, headers, config) {
                            var fragments = headers("location").split('/'),
                                slug = fragments[fragments.length - 1];
                            $location.url("/page/" + slug);
                        })
                        .error(function (data, status, headers, config) {
                            // TODO handle 409 conflict
                        });
                }
                else {
                    $scope.PageResource.save({ "slug": $scope.slug }, $scope.page);
                }
            };

            $scope.editThumbnails = function (image) {
                $rootScope.$broadcast('thumbnails:edit', image);
            }

            $scope.PageResource = $resource("/api/1.0/page/:slug");

            $scope.isNew = function () {
                return $scope.slug == "_new";
            };

            $scope.newPage = function () {
                return {
                    slug: "",
                    title: "",
                    addons: []
                };
            }

            $scope.reloadImages = function () {
                $scope.page.images = $http.get("/api/1.0/page/" + $scope.slug + "/image").success(function (data) {
                    $scope.page.images = data;
                });
            }

            $scope.getImageUploadUri = function () {
                return "/api/1.0/page/" + $scope.slug + "/attachment";
            }

            // Initialize existing page or new page

            if (!$scope.isNew()) {
                $scope.page = $scope.PageResource.get({
                    "slug": $scope.slug,
                    "expand": ["images"] }, function () {

                    if ($scope.page.published == null) {
                        // "null" does not seem to be evaluated properly in angular directives
                        // (like ng-show="something != null")
                        // Thus, we convert "null" published flag to undefined to be able to have that "high impedance"
                        // state in angular directives.
                        $scope.page.published = undefined;
                    }
                });
            }
            else {
                $scope.page = $scope.newPage();
            }

        }]);
