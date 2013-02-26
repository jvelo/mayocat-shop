(function () {

  function evalFn(element, scope, exp, property){
    property = property || 'changeOperation';
    return function (value) {
      var old = scope.hasOwnProperty(property) ? scope[property] : undefined;
      scope[property] = value;
      var retVal = scope.$eval(exp, element);
      scope[property] = old;
      return retVal;
    };
  }

  angular.module("jqui", [])
    .directive('sortableOnChange', function ($compile) {
      return {
        link: function (scope, item, attrs) {
          var sortableOnChangeExp = attrs.sortableOnChange || '',
              sortableOnChange = evalFn(item, scope, sortableOnChangeExp, 'changeOperation'),
              handle = attrs.handleClass ? "." + attrs.handleClass : false;

          item.sortable({
            handle: handle,
            update: function(event, ui) {
              var moved = ui.item.data('handle'),
                  target = ui.item.next().data('handle') ? ui.item.next().data('handle') : ui.item.prev().data('handle'),
                  position = ui.item.next().data('handle') ? "before" : "after";

              sortableOnChange({
                handle: moved,
                target: target,
                position: position
              });

              scope.$apply();
            }
          });

        }
      };
    });

}());
