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

Handlebars.registerHelper('include', function (templatename, options) {
    var partial = Handlebars.partials[templatename];
    var context = Object.extend(Object.extend({}, this), options.hash);
    return new Handlebars.SafeString(partial(context));
});