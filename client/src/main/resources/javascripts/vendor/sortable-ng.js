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
              sortableOnChange = evalFn(item, scope, sortableOnChangeExp, 'changeOperation');

          item.sortable({
            update: function(event, ui) {
              var moved = ui.item.data('handle'),
                  nextItem = ui.item.next().data('handle');

              sortableOnChange({ moved: moved, nextItem: nextItem });

              scope.$apply();
            }
          });

        }
      };
    });

}());
