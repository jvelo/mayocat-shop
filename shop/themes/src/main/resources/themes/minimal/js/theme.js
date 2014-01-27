/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
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
