/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {
    'use strict'

    angular.module('shipping', [])

        .factory('shippingService', ["$http", "$q", function ($http, $q) {

            var data,
                flatData,
                dataPromise,
                flatDataPromise;

            /**
             * Promise of earth data loaded from the server /api/shipping/destinations API.
             *
             * This data represents the graph of supported geographical destinations for shipping.
             */
            var loadData = function() {

                //if we already have a promise, just return that so it doesn't run twice.
                if (dataPromise) {
                    return dataPromise;
                }

                var deferred = $q.defer();
                dataPromise = deferred.promise;

                if (data) {
                    //if we already have data, return that.
                    deferred.resolve(data);
                } else {
                    $http.get('/api/shipping/destinations')
                        .success(function (result) {
                            data = result;
                            deferred.resolve({
                                name: "Earth",
                                children: data
                            });
                        })
                        .error(function () {
                            deferred.reject('Failed to load data');
                        })
                }
                return dataPromise;
            }

            /**
             * Promise of earth data loaded from the server /api/shipping/destinations/flat API.
             *
             * This is the same data as the one offered at /api/shipping/destinations/ but flattened in a map for fast
             * code -> name lookup
             */
            var getFlatData = function() {
                //if we already have a promise, just return that so it doesn't run twice.
                if (flatDataPromise) {
                    return flatDataPromise;
                }

                var deferred = $q.defer();
                flatDataPromise = deferred.promise;

                if (flatData) {
                    //if we already have data, return that.
                    deferred.resolve(flatData);
                }  else {
                    $http.get('/api/shipping/destinations/flat').success(function (result) {
                        flatData = result;

                        deferred.resolve( flatData );
                    })
                    .error(function () {
                            deferred.reject('Failed to load data');
                    })
                }

                return flatDataPromise;
            }

            /**
             * Convert a destination code (for example "FR" or "FR-49") to its matching destination name (here "France" and
             * "Maine Et Loire").
             *
             * Usage:
             * ------
             * > getName("FR").then(function(name){ console.log(name) }) // logs "France"
             *
             * @param code the code of the destination to get the name of
             * @return {String} a promise of the "pretty name" of the destination corresponding to the passed code.
             */
            var getName = function(code) {
                var deferred = $q.defer(),
                    promise = deferred.promise;

                getFlatData().then(function(data) {
                    promise.resolve(typeof data[code] !== 'undefined' ? data[code].name : undefined);
                });

                return promise;
            };

            /**
             * Same as #getName but takes a list of codes and returns a list of "pretty names".
             *
             * @param codes the list of codes to lookup names for
             * @returns {Array} the list of pretty names matching the list of codes.
             */
            var getNames = function (codes) {
                var deferred = $q.defer(),
                    promise = deferred.promise;

                getFlatData().then(function (data) {
                    var result = codes.map(function (code) {
                        return typeof data[code] !== 'undefined' ? data[code].name : undefined
                    });
                    deferred.resolve(result);
                });

                return promise;
            }

            return {
                getData: loadData,
                getName: getName,
                getNames: getNames
            };

        }])

        .directive('carrierSummary', [function () {
            /**
             * Displays the summary information associated with a carrier : zones eligible for delivery, transport
             * duration, pricing, etc.
             */
            return {
                templateUrl: "partials/directives/carrierSummary.html?v=4", // Defeat browser cache http://git.io/6dPuFQ
                scope: {
                    carrier: '=',
                    mainCurrency: '=',
                    weightUnit: '='
                },
                restrict: 'E',
                controller: function ($scope) {
                    $scope.getTranslationProperties = function () {
                        var carrier = $scope.carrier || {};

                        return {
                            numberOfDestinations: (carrier.destinations || {}).length || 0,
                            deliveryMaximumDays: carrier.maximumDays || 0,
                            costPerOrder: carrier.perShipping || 0,
                            costPerItem: carrier.perItem || 0,
                            mainCurrency: $scope.mainCurrency || ''
                        };
                    };
                }
            }
        }])

        .directive('destinationPicker', ["shippingService", function (shippingService) {

            /**
             * This is the code of a tired soul...
             *
             * I first tried to write it as a recursive pure angular directive without much success,
             * Then I gave birth painfully to this jQuery soup, that does the work awkwardly
             *
             * @param element
             * @param destinations
             * @param selected
             * @constructor
             */
            var DestinationPicker = function (element, destinations, selected) {

                this.selected = selected || [];

                this.nodeTemplate = " \
                  <li> \
                    <span class='arrow'></span> \
                    <span class='checkbox tristate'></span> \
                    <p class='destination' data-code=''><span class='name'></span></p> \
                    <div class='children hidden'><ul></ul></div> \
                  </li> \
                ";

                this.element = $(element).first();

                this.root = $("<ul />");
                this.element.append(this.root);

                this.updateSelection = function () {
                    this.selection = this.root.find(".checkbox.checked ~ p.destination").map(function (i, element) {
                        return $(element).data("code");
                    }).get();

                    this.onChange.call(this, this.selection);
                }

                this.checkParentBefore = function (node) {
                    var isChecked = node.children(".checkbox").first().hasClass("checked"),
                        parent = node.parents(":not(destination-picker) li").first();

                    if (parent.length == 1) {

                        var parentIsChecked = parent.find(".checkbox").first().hasClass("checked");

                        if (parentIsChecked) {
                            parent.find(".children > ul > li").addClass("checked");
                            parent.find(".children > ul > li > .checkbox").addClass("checked");
                        }

                        this.checkParentBefore(parent);
                    }
                };

                this.checkParentAfter = function (node) {
                    var isChecked = node.children(".checkbox").first().hasClass("checked"),
                        parent = node.parents(":not(destination-picker) li").first();

                    if (parent.length == 1) {
                        var allChildrenChecked = parent.find("> .children > ul > li > .checkbox:not(.checked)").length == 0,
                            atLeastOneChildrenChecked = parent.find("> .children > ul > li .checkbox.checked").length > 0

                        var code = node.find("p.destination").data("code");

                        if (!allChildrenChecked) {
                            parent.removeClass("checked");
                            parent.find(".checkbox").first().addClass("indeterminate").removeClass("checked");
                        }
                        else {
                            // All children are checked
                            parent.addClass("checked");
                            parent.find(".children .checkbox, .children li").removeClass("checked").removeClass("indeterminate");
                            parent.find(".checkbox").first().addClass("checked").removeClass("indeterminate");
                        }

                        if (!atLeastOneChildrenChecked) {
                            parent.removeClass("checked");
                            parent.find(".checkbox").first().removeClass("indeterminate").removeClass("checked");
                        }

                        this.checkParentAfter(parent);
                    }
                };

                this.addNode = function (data, parent) {
                    var node = $(this.nodeTemplate).clone(),
                        hasChildren = typeof data.children !== 'undefined' && data.children.length > 0,
                        picker = this;

                    node.find(".name").html(data.name);
                    node.find("p.destination").data("code", data.code);

                    node.find(".arrow").click(function (event) {

                        if (hasChildren && node.find(".children ul li").length == 0) {
                            for (var i = 0; i < data.children.length; i++) {
                                picker.addNode(data.children[i], node.find(".children ul").first());
                            }
                        }

                        node.children(".children").toggleClass("hidden");
                    })

                    node.children(".checkbox").click(function (event) {

                        picker.checkParentBefore(node);

                        if (node.children(".checkbox").hasClass("checked")) {

                        }
                        else if (node.children(".checkbox").hasClass("indeterminate")) {
                            node.find(".checkbox").removeClass("indeterminate").removeClass("checked");
                            node.find("li").removeClass("checked");
                        }
                        node.children(".checkbox").toggleClass("checked").removeClass("indeterminate");
                        node.toggleClass("checked");
                        picker.checkParentAfter(node);
                        picker.updateSelection();
                    });

                    parent.append(node);

                    if (hasChildren) {
                        node.find(".arrow").addClass("closed");
                    }

                    if (this.selected.indexOf(data.code) >= 0) {
                        picker.checkParentBefore(node);
                        node.children(".checkbox").toggleClass("checked");
                        node.toggleClass("checked");
                        node.parents(":not(destination-picker) li").children(".children").removeClass("hidden");
                    }

                    if (data.code.length === 3  && hasChildren) {
                        // this is a continent but not antartica
                        // pre-load all its countries
                        for (var i = 0; i < data.children.length; i++) {
                            picker.addNode(data.children[i], node.find(".children ul").first());
                        }
                    }

                    for (var i=0; i<this.selected.length; i++) {
                        var code = this.selected[i].substring(0, 2);
                        if (data.code.indexOf(code) == 0 && hasChildren && this.selected[i].length !== 3) {
                            for (var i = 0; i < data.children.length; i++) {
                                picker.addNode(data.children[i], node.find(".children ul").first());
                            }

                            node.parents(":not(destination-picker) li").children(".children").removeClass("hidden");
                        }
                    }
                }

                for (var i = 0; i < destinations.children.length; i++) {
                    this.addNode(destinations.children[i], this.root);
                }

                var picker = this;
                this.root.find("li.checked").each(function(i, node){
                    picker.checkParentAfter($(node));
                });

                this.onChange = function () {
                    // Change callback to be overriden by the directive
                }
            }

            return {
                scope: {
                    destination: '=',
                    model: "="
                },
                restrict: 'E',
                template: '<div></div>',

                controller: function ($scope, $element, $attrs) {
                    $scope.$watch("model", function () {
                        if ($scope.myselfUpdatingModel !== true) {
                            // The "model" value has been modified, but not by this very directive.
                            // Re-initialize the directive
                            $element.html("<div />");
                            shippingService.getData().then(function (data) {
                                var picker = new DestinationPicker($element.find("div"), data, $scope.model);
                                picker.onChange = function (destinations) {
                                    $scope.$apply(function ($scope) {
                                        $scope.model = destinations;
                                        // Flag to know the model has been modified by ourselves
                                        $scope.myselfUpdatingModel = true;
                                    })
                                }

                            });
                        }
                        $scope.myselfUpdatingModel = undefined;
                    });
                }
            }

        }])

})();
