/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/**
 * HTTP Auth Interceptor Module for AngularJS
 * (c) 2012 Witold Szczerba
 * License: MIT
 */
/**
 * Authentication module for mayocat, based on Witold Szczerba's http-auth-interceptor.
 *
 * See https://github.com/witoldsz/angular-http-auth/blob/master/src/http-auth-interceptor.js
 *
 * Some adaptations to work better with Mayocat.
 *
 * File as a whole is under the MPL license, and portions of it are under the MIT license.
 *
 * Copyright for portions of the file to their respective authors (see header above and the file on GitHub for the
 * exact portions authors.
 */
(function () {
    'use strict';

    angular.module('mayocat.authentication', ['http-auth-interceptor-buffer'])

        .factory('authenticationService', ['$rootScope', 'httpBuffer', function ($rootScope, httpBuffer) {
            return {
                /**
                 * call this function to indicate that authentication was successfull and trigger a
                 * retry of all deferred requests.
                 * @param data an optional argument to pass on to $broadcast which may be useful for
                 * example if you need to pass through details of the user that was logged in
                 */
                loginConfirmed: function (data) {
                    $rootScope.$broadcast('event:authenticationSuccessful', data);
                    httpBuffer.retryAll();
                }
            };
        }])

        /**
        * $http interceptor.
        * On 401 response (without 'ignoreAuthModule' option) stores the request
        * and broadcasts 'event:angular-auth-loginRequired'.
        */
        .config(['$httpProvider', function ($httpProvider) {

            var interceptor = ['$rootScope', '$q', 'httpBuffer', function ($rootScope, $q, httpBuffer) {
                function success(response) {
                    return response;
                }

                function error(response) {
                    if (response.status === 401 && !response.config.ignoreAuthModule
                        && response.config.url != '/api/login/') {
                        var deferred = $q.defer();
                        httpBuffer.append(response.config, deferred);
                        $rootScope.$broadcast('event:authenticationRequired');
                        return deferred.promise;
                    }
                    // otherwise, default behaviour
                    return $q.reject(response);
                }

                return function (promise) {
                    return promise.then(success, error);
                };

            }];
            $httpProvider.responseInterceptors.push(interceptor);
        }]);


    /**
     * Private module, an utility, required internally by 'http-auth-interceptor'.
     */
    angular.module('http-auth-interceptor-buffer', [])

        .factory('httpBuffer', ['$injector', function ($injector) {
            /** Holds all the requests, so they can be re-requested in future. */
            var buffer = [];

            /** Service initialized later because of circular dependency problem. */
            var $http;

            function retryHttpRequest(config, deferred) {
                function successCallback(response) {
                    deferred.resolve(response);
                }

                function errorCallback(response) {
                    deferred.reject(response);
                }

                $http = $http || $injector.get('$http');
                $http(config).then(successCallback, errorCallback);
            }

            return {
                /**
                 * Appends HTTP request configuration object with deferred response attached to buffer.
                 */
                append: function (config, deferred) {
                    buffer.push({
                        config: config,
                        deferred: deferred
                    });
                },

                /**
                 * Retries all the buffered requests clears the buffer.
                 */
                retryAll: function () {
                    for (var i = 0; i < buffer.length; ++i) {
                        retryHttpRequest(buffer[i].config, buffer[i].deferred);
                    }
                    buffer = [];
                }
            };
        }]);
})();