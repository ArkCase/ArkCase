'use strict';

/**
 * @ngdoc directive
 * @name format
 *
 * @description
 *
 * The format directive add decimal zeros and currency symbol to the input
 * and only allows numbers to be inserted
 *
 */
angular
    .module('directives')
    .directive('format', ['$filter', 'Config.LocaleService', function ($filter, LocaleService) {
        return {
            require: '?ngModel',
            link: function (scope, elem, attrs, ctrl) {
                if (!ctrl) return;

            ctrl.$formatters.unshift(function (a) {
                return $filter(attrs.format)(ctrl.$modelValue)
            });

            var numberRegExp = new RegExp("^[0-9.-][.0-9]*$");
            $(elem).keypress(function(event){
                if(!numberRegExp.test(event.target.value + event.key)){
                    return false;
                }
            });

            var currencySymbol = LocaleService.getCurrencySymbol();

            elem.bind('blur', function(event) {
                var plainNumber = elem.val().replace(/[^\d|\-+|\.+]/g, '');
                elem.val($filter(attrs.format)(plainNumber, currencySymbol));
            });
        }
    };
}]);