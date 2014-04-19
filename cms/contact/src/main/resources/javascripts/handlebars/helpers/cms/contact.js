/*
 * Copyright (c) 2012, Mayocat <hello@mayocat.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
(function (global, Mayocat) {

    if (typeof Mayocat.getComponent !== 'function') {
        throw "Missing dependency : Mayocat has not been initialized";
    }

    if (typeof out === "undefined") {
        // Can be useful for Q&D debugging
        // usage: out['println(java.lang.String)']("Hello " + something);
        var out = java.lang.System.out;
    }

    var tlsClass = org.mayocat.localization.ThemeLocalizationService,
        themeLocalizationService = Mayocat.getComponent(tlsClass);

    Handlebars.registerHelper('contactForm', function () {

        if (typeof this.flash !== 'undefined' && typeof this.flash.postContactMessage !== 'undefined') {
            // Form has been posted, display nothing
            return;
        }

        var options,
            context;

        if (arguments.length === 1) {
            context = this;
            options = arguments[0];
        } else {
            context = arguments[0];
            options = arguments[1];
        }

        // Right now fallback on the page URL. In the future, we should always have the current URI at the root scope
        var redirectsTo = options.hash['redirectsTo'] || this.page ? this.page.url : '/';

        var output = "<form action='/contact' method='post'> \
                        <input type='hidden' name='redirectTo' value='" + redirectsTo + "'>";

        if (typeof options.hash['subject'] !== 'undefined') {
            output += "<input type='hidden' name='subject' value='" + options.hash['subject'] + "'>"
        }

        output += options.fn(this);
        output += "</form>";

        return output;
    });

    Handlebars.registerHelper('contactField', function (context, options) {
        var type,
            tagName,
            output = '<',
            requiresClosing = false;

        if (typeof options.hash['type'] !== 'undefined') {
            type = options.hash['type'];
        }
        else {
            type = "text";
        }
        switch (type) {
            case "textarea":
                tagName = "textarea";
                requiresClosing = true;
                break;
            default:
                tagName = "input";
                break;
        }

        output += tagName;
        output += (" name='" + context + "'");

        for (var attribute in options.hash) {
            if (options.hash.hasOwnProperty(attribute)) {
                var value;
                if (attribute === "placeholder") {
                    value = options.hash[attribute];
                    // Placeholder is localizable
                    value = themeLocalizationService.getMessage(value, {}) || value;
                }
                else {
                    value = options.hash[attribute];
                }
                output += (" " + attribute + "=\"" + value + "\"");
            }
        }

        output += '>';

        if (requiresClosing) {
            output += ('</' + tagName + '>');
        }

        return output;
    });

    Handlebars.registerHelper('contactFormSuccess', function (options) {

        if (typeof this.flash === 'undefined') {
            // Don't arbitrate when the flash scope is not there : just return nothing
            return;
        }

        if (typeof this.flash.postContactMessage !== 'undefined' && this.flash.postContactMessage === 'Success') {
            return options.fn(this);
        }

        return options.inverse(this);
    });

})(this, Mayocat || {});