$(function () {

    // Mobile drawer menu

    enquire.register("screen and (max-width:719px)", {
        match: function () {
            var snapper = new Snap({
                element: document.getElementById('content'),
                disable: 'right',
                maxPosition: $(window).width() - 42
            });

            $("[data-toggle=left-drawer]").click(function () {
                // Toggle drawer when toggle button is clicked
                snapper[snapper.state().state == "left" ? "close" : "open"]("left");
            })
        }
    });

    // Image gallery slider

    if ($('#slider').length) {
        var swiper = Swipe(document.getElementById('slider'), {
            callback: function (index, elem) {
                $("ul.images li").removeClass("active");
                $($("ul.images li")[index]).addClass("active");
            }
        });
        $("ul.images li").click(function () {
            swiper.slide($(this).index());
        });
    }

    // Infinite scrolls

    if ($(".load-more").length) {
        var updating = false;

        $("#content").scroll(function () {

            if ($("#content").scrollTop() >= ($("#content > div").height() - $(window).height())) {

                var url = $(".load-more").data("next-page");

                if (updating || typeof url === "undefined") {
                    return;
                }

                updating = true;
                $(".load-more").removeClass("invisible").addClass("loading");

                $.getJSON(url, function (data) {

                    var products = typeof data.collection !== 'undefined' ? data.collection.products : data.products;

                    for (var i = 0; i < products.list.length; i++) {
                        var html = Mayocat.render("#product-in-list-template", products.list[i]);

                        $(".products-list").append(html);
                    }

                    if (products.pagination.nextPage) {
                        $(".load-more").data("next-page", products.pagination.nextPage.url);
                    }
                    else {
                        $(".load-more").remove();
                    }

                    $(".load-more").addClass("invisible").removeClass("loading");

                    updating = false;
                });
            }
        });
    }
});
