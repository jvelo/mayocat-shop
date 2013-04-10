var global = this;

(function ()
{

    var ThemeManager = org.mayocat.theme.ThemeManager,
        Breakpoint = Packages.org.mayocat.theme.Breakpoint;

    Object.extend = function (destination, source)
    {
        for (var property in source) {
            if (source[property] && source[property].constructor &&
                source[property].constructor === Object)
            {
                destination[property] = destination[property] || {};
                arguments.callee(destination[property], source[property]);
            } else {
                destination[property] = source[property];
            }
        }
        return destination;
    };

    var getComponent = function (clazz, hint)
    {
        hint = typeof hint === "undefined" ? "default" : hint;
        return org.mayocat.util.Utils.getComponent(clazz, hint);
    };

    Handlebars.registerHelper('include', function (template, options)
    {
        var partial = Handlebars.partials[template];
        var context = Object.extend(Object.extend({}, this), options.hash);
        if (typeof partial === "function") {
            return new Handlebars.SafeString(partial(context));
        }
        return "Partial not found : " + template;
    });

    Handlebars.registerHelper('includeTemplate', function (template, options)
    {
        var themeManager = getComponent(ThemeManager);
        var resolved = themeManager.resolveTemplate(template, Breakpoint.DEFAULT);

        var name = resolved.getId(),
            content = String(resolved.getContent());

        global.templates[name] = Handlebars.compile(content, {noEscape: true});
        Handlebars.registerPartial(name, global.templates[name]);

        var partial = Handlebars.partials[resolved.getId()];
        var context = Object.extend(Object.extend({}, this), options.hash);
        if (typeof partial === "function") {
            return new Handlebars.SafeString(partial(context));
        }
        return "Partial not found : [" + template + "] with id [" + resolved.getId() + "]";
    });

    Handlebars.registerHelper('resource', function (path, options)
    {
        return "/resources/" + path;
    });

    Handlebars.registerHelper('isPath', function (path, options)
    {
        if (typeof this.location !== "undefined") {
            if (path === this.location.path) {
                return options.fn(this);
            }
        }
        return options.inverse(this);
    });

})();