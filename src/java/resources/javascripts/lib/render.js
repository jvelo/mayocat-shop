// Compile passed template
var template = Handlebars.compile(template);

// Parse context as JS object
context = JSON.parse(contextAsJSON);

// Evaluate template against context
template(context);
