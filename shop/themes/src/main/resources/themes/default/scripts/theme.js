(function () {
    'use strict';

    /**
     * Carousel of images (used on home page for example).
     */
    (function ($) {
        $.fn.carousel = function () {

            var setLeftPropertyWithoutTransition = function (element, left) {
                element.addClass("notransition");
                element.css("left", left + "px");
                element.height(); // force a reflow
                element.removeClass("notransition");
            }

            this.each(function () {
                var carousel = $(this).children("ul"),
                    controls = $(this).find(".controls"),
                    bullets = controls.find(".bullets"),
                    titleContainer = controls.find("h2"),
                    photos = carousel.find("li"),
                    left = -960 * (photos.length + 1),
                    isTransitioning = false;

                for (var i = 0; i < photos.length; i++) {
                    var bullet = $("<li />");
                    if (i == 0) {
                        bullet.addClass("active");
                    }
                    bullets.append(bullet);
                }

                titleContainer.text($(photos.get(1)).find("img").attr("title"));
                controls.removeClass("hidden");
                photos.clone().appendTo(carousel);


                setLeftPropertyWithoutTransition(carousel, left);

                carousel.bind("transitionend webkitTransitionEnd oTransitionEnd MSTransitionEnd", function () {
                    isTransitioning = false;
                    var left = parseInt(carousel.css("left")),
                        foreLast = -960 * (photos.length * 2 - 2),
                        index = ((-left / 960 ) - 1) % photos.length,
                        title = $(photos.get((index + 1) % photos.length)).find("img").attr("title");

                    titleContainer.text(title);
                    bullets.find("li").removeClass("active");
                    $(bullets.find("li").get(index)).addClass("active");

                    if (left == -960) { // First image
                        left = -960 * (photos.length + 1);
                        setLeftPropertyWithoutTransition(carousel, left);
                    }
                    else if (left == foreLast) { // Forelast image
                        left = -960 * (photos.length - 2);
                        setLeftPropertyWithoutTransition(carousel, left);
                    }
                });

                $(this).find(".left, .right").click(function (event) {
                    if (isTransitioning) {
                        return;
                    }
                    isTransitioning = true;
                    var left = parseInt(carousel.css("left")),
                        shift = $(event.target).hasClass("left") ? 960 : -960,
                        newLeft = left + shift;

                    carousel.css("left", newLeft + "px");
                });
            });
        }
    }(jQuery));

    /**
     * Image gallery used on product pages
     */
    (function ($) {
        $.fn.gallery = function () {
            this.each(function () {
                var container = $(this),
                    featured = container.find(".featured img"),
                    thumbnails = container.find(".thumbnail"),
                    thumbnailClass = "thumbnail",
                    thumbnailActiveClass = "thumbnail active";

                $(thumbnails).click(function (event) {
                    var target = event.target || event.eventSrc;
                    if (target.tagName.toLowerCase() != "li") {
                        // in case the event target is the image, take its parent li
                        target = target.parentNode;
                    }

                    $(featured).attr("src", $(target).find("img").data("image-src"));

                    $(target).siblings().removeClass("active");
                    $(target).addClass("active");
                });

                // Pre-load all big images\
                var hidden = $(container).append($("<div class='hidden'></div>")).find(".hidden");
                for (var i = 0; i < thumbnails.length; i++) {
                    var src = ($(thumbnails[i]).find("img").data("image-src"));
                    var image = $("<img />");
                    image.attr("src", src);
                    $(hidden).append(image);
                }

                $(thumbnails).first().addClass("active");
                $(container).addClass("initialized");
            });
        }
    }(jQuery));

    $(document).ready(function () {
        $(".gallery").gallery();
        $(".carousel").carousel();
    });
})();