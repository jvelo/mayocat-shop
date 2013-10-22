'use strict';

angular.module('account', ['ngResource'])

    .controller('AccountSettings', ['$scope', '$modal', function($scope, $modal) {

        $scope.changeMyPassword = function(){
            $scope.modalInstance = $modal.open({
                templateUrl: 'changeMyPassword.html'
            });
            $scope.modalInstance.result.then($scope.deleteArticle);
        }

    }]);


