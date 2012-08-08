'use strict';

var mayocat = angular.module('mayocat', []);

// config(['$locationProvider', function($locationProvider) {
//   $locationProvider.html5Mode(true);
// }]).
mayocat.config(['$routeProvider', function($routeProvider) {
  $routeProvider.
      when('/', {templateUrl: 'partials/home.html',   controller: HomeCtrl}).
      otherwise({redirectTo: '/'});
}]);

/**
 * Authentication/401 interception
 *
 * based on http://www.espeo.pl/2012/02/26/authentication-in-angularjs-application
 */
mayocat.config(function($httpProvider) {
  var interceptor = ['$rootScope','$q', function(scope, $q) {

    function success(response) {
      return response;
    }

    function error(response) {
      var status = response.status;
      if (status == 401) {
        var deferred = $q.defer();
        var req = {
          config: response.config,
          deferred: deferred
        }
        scope.requests401.push(req);
        scope.$broadcast('event:authenticationRequired');
        return deferred.promise;
      }
      // otherwise
      return $q.reject(response);
    }

    return function(promise) {
      return promise.then(success, error);
    }
  }];
  $httpProvider.responseInterceptors.push(interceptor);
});

mayocat.run(['$rootScope', '$http', function(scope, $http) {

  /**
   * Holds all the requests which failed due to 401 response.
   */
  scope.requests401 = [];

  /**
   * On 'event:authenticationSuccessful', resend all the 401 requests.
   */
  scope.$on('event:authenticationSuccessful', function() {
    var i, requests = scope.requests401;
    for (i = 0; i < requests.length; i++) {
      retry(requests[i]);
    }
    scope.requests401 = [];
    function retry(req) {
      $http(req.config).then(function(response) {
        req.deferred.resolve(response);
      });
    }
  });

  /**
   * On 'event:loginRequest' send credentials to the server.
   */
  scope.$on('event:authenticationRequest', function(event, username, password) {
    var config = {
      headers: {'Authorization': 'Basic ' + window.btoa(username + ":" + password)}
    }
    $http.get('/user/_me', config).success(function(data, status) {
      if (status == 200) {
        scope.$broadcast('event:authenticationSuccessful', data.email);
      }
      else {
        scope.$broadcast('event:authenticationFailure');
      }
    });
  });

  /**
   * On 'logoutRequest' invoke logout on the server and broadcast 'event:loginRequired'.
   */
  scope.$on('event:logoutRequest', function() {
    $http.put('j_spring_security_logout', {}).success(function() {
      ping();
    });
  });

  /**
   * Ping server to figure out if user is already logged in.
   */
  function ping() {
    $http.get('/user/_me').success(function() {
      scope.$broadcast('event:loginConfirmed');
    });
  }

  ping();
}]);
