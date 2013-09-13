'use strict'

angular.module('entities', [])

    .factory('entityBaseMixin', function ($routeParams) {
        return function(entityType) {
            var mixin = {};

            mixin.slug = $routeParams[entityType];

            mixin[entityType];
            return mixin;
        }
     })

    .factory('entityModelMixin', function (configurationService) {
        return function (entityType, scope) {
            var mixin = {};

            mixin.initializeModels = function () {
                scope.models = [];
                configurationService.get("entities", function (entities) {
                    if (typeof entities[entityType] !== 'undefined') {
                        for (var modelId in entities[entityType].models) {
                            if (entities[entityType].models.hasOwnProperty(modelId)) {
                                var model = entities[entityType].models[modelId];
                                scope.models.push({
                                    id: modelId,
                                    name: model.name
                                });
                            }
                        }
                    }
                });
            }
            return mixin;
        }
    })

    .factory('entityAddonsMixin', function (addonsService) {
        return function(entityType, scope) {
            var mixin = {};

            mixin.initializeAddons = function () {
                addonsService.initializeEntityAddons(entityType, scope[entityType]).then(function (addons) {
                    scope.addons = addons;
                });
            }

            return mixin;
        }
    })

    .factory('entityLocalizationMixin', function (addonsService) {
        return function(entityType, scope) {
            var mixin = {};

            return mixin;
        }
    })

    .factory('entityImageMixin', function (imageService, $http) {
        return function (entityType, scope) {
            var mixin = {};

            mixin.editThumbnails = function (image) {
                mixin.$emit('thumbnails:edit', entityType, image);
            }

            mixin.reloadImages = function () {
                $http.get("/api/" + entityType + "s/" + scope.slug + "/images")
                    .success(function (data) {
                        scope[entityType].images = data;
                    }
                );
            }

            mixin.selectFeatureImage = function (image) {
                imageService.selectFeatured(scope[entityType], image);
            }

            mixin.removeImage = function (image) {
                $http.delete("/api/products/" + scope.slug + "/images/" + image.slug).success(function () {
                    mixin.reloadImages();
                });
            }

            mixin.getImageUploadUri = function () {
                return "/api/" + entityType + "s/" + scope.slug + "/attachments";
            }

            return mixin;
        }
    });

angular.module('page', ['ngResource'])

    .controller('PageController', [
        '$scope',
        '$rootScope',
        '$resource',
        '$http',
        '$location',
        'entityBaseMixin',
        'entityImageMixin',
        'entityAddonsMixin',
        'entityModelMixin',

        function ($scope,
                  $rootScope,
                  $resource,
                  $http,
                  $location,
                  entityBaseMixin,
                  entityImageMixin,
                  entityAddonsMixin,
                  entityModelMixin) {

            angular.extend($scope, entityBaseMixin("page", $scope));
            angular.extend($scope, entityAddonsMixin("page", $scope));
            angular.extend($scope, entityModelMixin("page", $scope));
            angular.extend($scope, entityImageMixin("page", $scope));

            $scope.publishPage = function () {
                $scope.page.published = true;
                $scope.updatePage();
            }

            $scope.updatePage = function () {
                $scope.isSaving = true;
                if ($scope.isNew()) {
                    $http.post("/api/pages/", $scope.page)
                        .success(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            var fragments = headers("location").split('/'),
                                slug = fragments[fragments.length - 1];
                            $rootScope.$broadcast('pages:refreshList');
                            $location.url("/pages/" + slug);
                        })
                        .error(function (data, status, headers, config) {
                            $scope.isSaving = false;
                            // TODO handle 409 conflict
                        });
                }
                else {
                    $scope.PageResource.save({ "slug": $scope.slug }, $scope.page, function () {
                        $scope.isSaving = false;
                        $rootScope.$broadcast('pages:refreshList');
                    });
                }
            };

            $scope.PageResource = $resource("/api/pages/:slug");

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

            // Initialize existing page or new page

            if (!$scope.isNew()) {
                $scope.page = $scope.PageResource.get({
                    "slug": $scope.slug,
                    "expand": ["images"] }, function () {

                    $scope.initializeAddons();
                    $scope.initializeModels();

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
                $scope.initializeAddons();
                $scope.initializeModels();
            }

            $scope.confirmDeletion = function () {
                $rootScope.$broadcast('page:confirmDelete');
            }

            $scope.deletePage = function () {
                $scope.PageResource.delete({
                    "slug": $scope.slug
                }, function () {
                    $rootScope.$broadcast('page:dismissConfirmDelete');
                    $rootScope.$broadcast('pages:refreshList');
                    $location.url("/contents");
                });
            }

        }]);
