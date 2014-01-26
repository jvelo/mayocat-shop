$(function () {
    enquire.register("screen and (max-width:899px)", {
        setup: function () {
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
});
