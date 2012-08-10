'use strict'

function HomeCtrl($scope) {
}

function UserCtrl($rootScope, $scope) {
  $scope.user = "";
  $scope.logout = function() {
    $rootScope.$broadcast("event:forgetAuthenticationRequest");
  };
  $scope.$on("event:authenticationSuccessful", function(event, user) {
    $scope.user = user;
  });
}

function LoginCtrl($rootScope, $scope) {
  $scope.username = "";
  $scope.password = "";
  $scope.remember = false;
  $scope.requestLogin = function() {
    $rootScope.$broadcast("event:authenticationRequest", $scope.username, $scope.password, $scope.remember);
  };
}

function AppController($scope) {
  $scope.authenticated = undefined;
  $scope.$on("event:authenticationRequired", function() {
    $scope.authenticated = false;
  });
  $scope.$on("event:authenticationSuccessful", function() {
    $scope.authenticated = true;
  });
}
