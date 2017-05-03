angular.module('angular-bootstrap-select', []).directive('abSelect',
  function ($timeout, $log) {
    return {
      restrict: 'EA',
      replace: true,
      require: ['?ngModel', '?ngOptions'],
      scope: false,
      transclude: false,
      templateUrl: 'angular-bootstrap-select/select.html',
      link: function(scope, element, attrs, ngModel) {
        $log.debug('Angular Select Bootstrap', scope);
        if(!ngModel) {
          return;
        }

        function refresh(newVal) {
          $log.debug('New Val', newVal);
          if(angular.isUndefined(newVal)) {
            return;
          }

          scope.$applyAsync(function () {
            if (attrs.ngOptions && /track by/.test(attrs.ngOptions)) {
              element.selectpicker('val', newVal);
            }
            element.selectpicker('refresh');
          });
        }

        $timeout(function() {
          element.selectpicker(attrs);
          element.selectpicker('refresh');
          element.selectpicker('render');
        });

        if (attrs.ngOptions) {
          scope.$watch(attrs.ngOptions, refresh, true);
        }

        if (attrs.ngModel) {
          scope.$watch(attrs.ngModel, refresh, true);
        }

        if (attrs.ngDisabled) {
          scope.$watch(attrs.ngDisabled, refresh, true);
        }

        scope.$watch('selected', function(val) {
          if(!val) {
            return;
          }
          $log.debug('Val', val);
        }, true);

        scope.$on('$destroy', function () {
          $timeout(function () {
            element.selectpicker('destroy');
          });
        });
      }
    };
  }
);

angular.module('angular-bootstrap-select').factory('selectHelpers', function() {
  return {

  };
});

angular.module('angular-bootstrap-select').run(['$templateCache', function($templateCache) {
  $templateCache.put('angular-bootstrap-select/select.html',
    '<select class=\"angular-select\">\n    <option style=\"display:none\" value=\"\">Select</option>\n<select>\n'
  );
}]);