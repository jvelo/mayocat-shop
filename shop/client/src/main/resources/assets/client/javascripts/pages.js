'use strict'

angular.module('pages', [])

    .controller('PagesController', ['$scope', '$resource',
        function ($scope, $resource) {

            $resource("/api/1.0/page").get({}, function (pages){
               $scope.pages = pages.items;
            });

        }]);
