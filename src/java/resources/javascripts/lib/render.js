// Compile passed template
var template = Handlebars.compile(template),
    context;

// Parse context as JS object
if (typeof JSON !== 'undefined') {
  context = JSON.parse(contextAsJSON);
}
else {
  context = eval("(" + contextAsJSON + ")");
}

// Evaluate template against context
template(context);
