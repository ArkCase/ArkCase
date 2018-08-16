'use strict';

/**
 * @ngdoc directive
 * @name formatCurrency
 *
 * @description
 *
 * The formatCurrency directive add decimal zeros and currency symbol to the input
 * and only allows numbers to be inserted
 *
 */
angular
    .module('directives')
    .directive('formatCurrency', ['$filter', 'Config.LocaleService', 'UtilService', function ($filter, LocaleService, Util) {
        return {
            require: '?ngModel',
            link: function (scope, elem, attrs, ctrl) {
                if (!ctrl) return;

            var numberRegExp = new RegExp("^[0-9-][.0-9]*$");
            $(elem).keypress(function(event){
                if(!numberRegExp.test(event.target.value + event.key)){
                    return false;
                }
            });

            var currencySymbol = LocaleService.getCurrencySymbol();

            elem.bind('blur', function(event) {
                var plainNumber = elem.val().replace(/[^\d|\-+|\.+]/g, '');
                if(!Util.isEmpty(plainNumber) && plainNumber != 0) {
                    elem.val($filter("currency")(plainNumber, currencySymbol));
                }
                if(plainNumber == 0){
                    elem.val('');
                }
            });

        }
    };
}]);