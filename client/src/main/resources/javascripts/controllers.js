'use strict'

function HomeCtrl($scope) {
}

function LoginCtrl($rootScope, $scope) {
  $scope.username = "";
  $scope.password = "";
  $scope.remember = false;
  $scope.authenticationFailed = false;
  $scope.requestLogin = function() {
    $rootScope.$broadcast("event:authenticationRequest", $scope.username, $scope.password, $scope.remember);
  };
  $scope.$on("event:authenticationFailure", function() {
    $scope.authenticationFailed = true;
  });
  $scope.$on("event:authenticationSuccessful", function(event, data) {
    $scope.authenticationFailed = false;
  });
}

function AppController($rootScope, $scope) {

  $scope.tenant = undefined;
  $scope.user = undefined;
  $scope.authenticated = undefined;

  $scope.logout = function() {
    $rootScope.$broadcast("event:forgetAuthenticationRequest");
  };

  $scope.$on("event:authenticationRequired", function() {
    $scope.authenticated = false;
  });

  $scope.$on("event:authenticationSuccessful", function(event, data) {
    $scope.authenticated = true;
    $scope.user = data.user;
    $scope.tenant = data.tenant;
  });
}
