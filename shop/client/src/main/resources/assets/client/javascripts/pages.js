'use strict'

angular.module('pages', [])

    .controller('PagesController', ['$scope', '$resource',
        function ($scope, $resource) {

            $scope.refreshPages = function () {
                $resource("/api/pages").get({}, function (pages) {
                    $scope.pages = pages.items;
                });
            }

            $scope.$on("pages:refreshList", function () {
                $scope.refreshPages();
            });

            $scope.refreshPages();

        }]);
