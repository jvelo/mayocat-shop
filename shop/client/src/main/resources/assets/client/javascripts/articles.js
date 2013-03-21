'use strict'

angular.module('articles', [])

    .controller('ArticlesController', ['$scope', '$resource',
    function ($scope, $resource) {

        $resource("/api/1.0/news").get({}, function (articles){
            $scope.articles = articles.items;
        });

    }]);
