/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function () {

    'use strict'

    var capitalize = function (string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    };


    angular.module('mayocat.mixins', [])

        //==============================================================================================================
        //
        // Mixin service that can be use to share functionality between controllers.
        //
        // Usage:
        //
        // 1. Export module mixins definition:
        //
        //  angular.module('my.moodule', [])
        //
        // .factory('myModuleMixins', [
        //    'mixins', // This is our service
        //    'myModuleBaseMixin', // An actual mixin that can be injected in a controller
        //    'myModuleOtherMixin', // An other mixn
        //    'myModuleLastMixin', // A last one
        //    function (mixins, base, other last) {
        //        return mixins({
        //            base: base,
        //            model: model,
        //            addons: addons,
        //            localization: localization,
        //            image: image
        //        });
        //    }
        //])
        //
        // 2. Mixin consumption
        //
        // .controller('MyController', ['$scope', [...] 'myModuleMixins', function ($scope, [...], mixins) {
        //
        //   // Example: mix in individual mixins (with configuration options)
        //   mixins.extend(["base", "other"], $scope, {
        //    "base": {
        //        "some": "option,
        //    },
        //    "other" : {
        //        "other" : "option"
        //    }
        // });
        //
        .factory('mixins', [function(){
            return function (allMixins) {
                 var args = arguments;
                return {
                    extendAll: function ($scope, globalOptions, mixinOptions) {

                        // Make sure the options hash exists.
                        mixinOptions = typeof mixinOptions === "undefined" ? {} : mixinOptions;

                        // Iterate over all mixins, find its option object in the global option hash, and then extend
                        // the passed scope with it.

                        for (var i = 0; i < Object.keys(allMixins).length; i++) {
                            var mixin = Object.keys(allMixins)[i];
                            var options = mixinOptions[mixin];
                            if (typeof globalOptions !== 'undefined') {
                                angular.extend($scope, allMixins[mixin](globalOptions, options));
                            }
                            else {
                                angular.extend($scope, allMixins[mixin](options));
                            }
                        }
                    },

                    extend: function(mixins, $scope, globalOptions, mixinOptions) {
                        mixinOptions = typeof mixinOptions === "undefined" ? {} : mixinOptions;

                        // Support for "just one mixin"
                        if (typeof mixinOptions == "string") {
                            mixinOptions = {
                                mixins: mixinOptions
                            };
                            mixins = [ mixins ];
                        }

                        for (var i = 0; i < Object.keys(allMixins).length; i++) {
                            var mixin = Object.keys(allMixins)[i];
                            if (mixins.indexOf(mixin) >= 0) {
                                var options = mixinOptions[mixin];
                                if (typeof globalOptions !== 'undefined') {
                                    angular.extend($scope, allMixins[mixin](globalOptions, options));
                                }
                                else {
                                    angular.extend($scope, allMixins[mixin](options));
                                }

                            }
                        }
                    }
                }
            };
        }]);

})();

