'use strict';

var mayocat = angular.module('mayocat', ['search']);

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
  scope.$on('event:authenticationRequest', function(event, username, password, remember) {
    var config = {
      headers: {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
    };
    var data = {
      username: username,
      password: password,
      remember: remember
    };
    $http.post('/login/', $.param(data), config).success(function(data, status) {
      if (status == 200) {
        scope.$broadcast('event:authenticationSuccessful', data.email);
      }
      else {
        scope.$broadcast('event:authenticationFailure');
      }
    });
  });

  /**
   * On 'logoutRequest' invoke logout on the server and broadcast 'event:authenticationRequired'.
   */
  scope.$on('event:forgetAuthenticationRequest', function() {
    var config = {
      headers: {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
    };
    $http.post('/logout/', "", config).success(function() {
      ping();
      scope.$broadcast('event:authenticationRequired');
    });
  });

  /**
   * Ping server to figure out if user is already logged in.
   */
  function ping() {
    $http.get('/user/_me').success(function(data) {
      scope.$broadcast('event:authenticationSuccessful', data.email);
    });
  }

  ping();
}]);
