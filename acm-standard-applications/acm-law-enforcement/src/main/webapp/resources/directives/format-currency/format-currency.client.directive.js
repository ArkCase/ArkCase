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
            scope: {
                isValidAmount: '=',
                amount: '='
            },
            link: function (scope, elem, attrs, ctrl) {
                if (!ctrl) return;

                var numberRegExp = new RegExp("^-?\\d{1,}$|^-?\\d{1,}\\.\\d{1,2}$");

                var keyDownExecuted = false;
                var strKeyPress = "";
                var str = "";
                $(elem).keypress(function(event){
                    if (!keyDownExecuted) {
                        strKeyPress = event.target.value;
                    }else {
                        return false;
                    }
                    keyDownExecuted = true;
                });

                $(elem).keyup(function(event){
                    str = event.target.value;
                    if((str.length > 1 ||
                            (str.length === 1 && str !== '-')) &&
                        !str.endsWith('.') &&
                        !numberRegExp.test(str)){
                        str = strKeyPress;
                        elem.val(str);
                    }
                    keyDownExecuted = false;
                    scope.amount = str;
                });

                var currencySymbol = LocaleService.getCurrencySymbol();

                elem.bind('blur', function(event) {
                    if(!numberRegExp.test(str)){
                        scope.isValidAmount = false;
                        return false;
                    }
                    scope.isValidAmount = true;
                    var plainNumber = elem.val().replace(/[^\d|\-+|\.+]/g, '');
                    if(!Util.isEmpty(plainNumber) && plainNumber != 0) {
                        elem.val($filter("currency")(plainNumber, currencySymbol));
                    }
                    if(plainNumber == 0){
                        elem.val('');
                    }
                });


                elem.bind('focus', function(event) {
                    str = elem.val().replace(/\$|,|\./g, "");
                    elem.val(str);
                });
            }
        };
    }]);