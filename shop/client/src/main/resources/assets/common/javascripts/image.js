(function () {
    'use strict'

    angular.module('mayocat.image', [])
        .factory('imageService', function () {

            return {

                selectFeatured: function (entity, image) {
                    for (var img in entity.images) {
                        if (entity.images.hasOwnProperty(img)) {
                            if (entity.images[img].href === image.href) {
                                entity.images[img].featured = true;
                                entity.featuredImage = entity.images[img];
                            }
                            else {
                                entity.images[img].featured = false;
                            }
                        }
                    }
                }

            };

        });

})();