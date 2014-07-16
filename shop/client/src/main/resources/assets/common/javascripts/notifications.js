/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {

    'use strict';

    angular.module('mayocat.notifications', [])

        .service('notificationService', ['$rootScope', function($rootScope) {

            this.notify = function(message, options) {
                $rootScope.$emit('notifications:add', message, options);
            };

        }])

        .directive('notifications', ['$rootScope', '$location', '$timeout', 'notificationService',
            function($rootScope, $location, $timeout, notificationService) {

                function controller($scope) {
                    var basicNotificationsQueue = [];

                    $scope.basicNotifications = [];
                    $scope.progressNotifications = [];
                    $scope.allMinified = false;

                    /**
                     * Adds a new notification.
                     * @param {String}   message          The message to display.
                     * @param {Object}   options          The options for the notification.
                     */
                    function add(message, options) {
                        var validTypes = ['basic', 'progress'],
                            validLevels = ['info', 'error'],
                            options = options || {};

                        // Create the notification.
                        var notification = {
                            message: message,
                            type: ~validTypes.indexOf(options.type) ? options.type : 'basic',
                            level: ~validLevels.indexOf(options.level) ? options.level : 'info',
                            origin: $location.path(),
                            minified: false,
                            progress: 0
                        };

                        // Add it to the queue if it's a basic notification. If it's a progress notification, add it to the
                        // scope.
                        if (notification.type == 'basic') {
                            basicNotificationsQueue.push(notification);
                        } else {
                            $scope.progressNotifications.push(notification);

                            // Return some controls.
                            if (typeof options.controls == 'function') {
                                options.controls(
                                    function(percentage) {
                                        return setProgress(notification, percentage);
                                    },
                                    function() {
                                        return dismiss(notification);
                                    }
                                );
                            }
                        }

                        refreshQueue();
                    }

                    // Dismisses a notification and returns true if everything went allright.
                    function dismiss(notification) {
                        var lists = [$scope.basicNotifications, $scope.progressNotifications];

                        for (var i = 0, list, index; list = lists[i++];) {
                            index = list.indexOf(notification);

                            if (index != -1) {
                                list.splice(index, 1);
                                refreshQueue();
                                return true;
                            }
                        }

                        return false;
                    }

                    // Updates a notification and returns true if everything went allright.
                    function setProgress(notification, percentage) {
                        var index = $scope.progressNotifications.indexOf(notification);

                        // Remove the notification if we are at 100%, otherwise, update the percentage.
                        if (percentage >= 100) {
                            return dismiss(notification);
                        } else {
                            if (index != -1) {
                                $scope.progressNotifications[index].progress = percentage;
                                $scope.$apply();
                                return true;
                            }

                            return false;
                        }
                    }

                    // If necessary, moves a notification from the queue to the active notification list.
                    function refreshQueue() {
                        // If the active list is empty and the queue is not, move a notification.
                        if (!$scope.basicNotifications.length && basicNotificationsQueue.length) {
                            var notification = basicNotificationsQueue.shift();
                            $scope.basicNotifications.push(notification);

                            // Dismiss it automatically.
                            $timeout(function() {
                                dismiss(notification);
                            }, 4000);
                        }

                        // If one basic notification is displayed, reduce the progress ones.
                        $scope.allMinified = !!$scope.basicNotifications.length;

                        $scope.$apply();
                    }

                    // Register listeners.
                    $rootScope.$on('notifications:add', function() {
                        // Extract and pass the parameters.
                        var params = Array.prototype.slice.call(arguments, 1);
                        add.apply(null, params);
                    });

                    $scope.$on('$routeChangeSuccess', function() {
                        $scope.progressNotifications.forEach(function(notification) {
                            notification.minified = ($location.path() != notification.origin);
                        });
                    });
                }

                return {
                    restrict: 'E',
                    scope: {
                        reduce: '='
                    },
                    controller: controller,
                    templateUrl: '/common/partials/notifications.html'
                };

            }]);

})();