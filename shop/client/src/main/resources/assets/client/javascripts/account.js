'use strict';

angular.module('account', ['ngResource'])

    .directive("passwordVerify", function () {
        return {
            require: "ngModel",
            scope: {
                passwordVerify: '='
            },
            link: function (scope, element, attrs, ctrl) {
                scope.$watch(function () {
                    var combined;

                    if (scope.passwordVerify || ctrl.$viewValue) {
                        combined = scope.passwordVerify + '_' + ctrl.$viewValue;
                    }
                    return combined;
                }, function (value) {
                    if (value) {
                        ctrl.$parsers.unshift(function (viewValue) {
                            var origin = scope.passwordVerify;
                            if (origin !== viewValue) {
                                ctrl.$setValidity("passwordVerify", false);
                                return undefined;
                            } else {
                                ctrl.$setValidity("passwordVerify", true);
                                return viewValue;
                            }
                        });
                    }
                });
            }
        };
    })

    .controller('AccountSettings', ['$scope', '$modal', function ($scope, $modal) {

        $scope.changeMyPassword = function () {
            $scope.modalInstance = $modal.open({
                templateUrl: 'changeMyPassword.html',
                controller:['$scope', '$http', '$modalInstance', function ($scope, $http, $modalInstance) {
                    $scope.request = {};
                    $scope.updatePassword = function () {
                        $http.post("/api/me/password/", {
                            currentPassword: $scope.currentPassword,
                            newPassword: $scope.newPassword
                        }).success(function (response, status) {
                                if (status == 401) {
                                    $scope.isWrongPassword = true;
                                }
                                else {
                                    $modalInstance.close();
                                }
                        })
                    }
                }]
            });

        }

    }]);


