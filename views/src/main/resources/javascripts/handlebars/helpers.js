Object.extend = function(destination, source) {
    for (var property in source) {
        if (source[property] && source[property].constructor &&
            source[property].constructor === Object) {
            destination[property] = destination[property] || {};
            arguments.callee(destination[property], source[property]);
        } else {
            destination[property] = source[property];
        }
    }
    return destination;
};

Handlebars.registerHelper('include', function (template, options) {
    var partial = Handlebars.partials[template];
    var context = Object.extend(Object.extend({}, this), options.hash);
    if (typeof partial === "function") {
      return new Handlebars.SafeString(partial(context));
    }
    return "Partial not found : " + template;
});

Handlebars.registerHelper('resource', function (path, options) {
    return "/resources/"  + path;
});