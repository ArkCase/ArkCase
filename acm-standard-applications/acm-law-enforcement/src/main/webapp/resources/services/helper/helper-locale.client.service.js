'use strict';

/**
 * @ngdoc service
 * @name services:Helper.LocaleService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/helper/helper-locale.client.service.js services/helper/helper-locale.client.service.js}
 *
 * Helper.LocaleService save locale code in scope for later use
 */
angular.module('services').factory('Helper.LocaleService', ['$locale', 'UtilService', 'Config.LocaleService'
    , function ($locale, Util, LocaleService) {

        var Service = {

            /**
             * @ngdoc method
             * @name Component Constructor
             * @methodOf services:Helper.LocaleService
             *
             * @param {Object} arg Map arguments
             * @param {Object} arg.scope Angular $scope
             * @param {Function} (Optional)arg.onTranslateChangeSuccess Event handler of '$translateChangeSuccess'
             *
             * @description
             * Helper.LocaleService save locale code as variable 'lang' in scope for later use.
             * For example, it can be used in 'translateData' filter.
             *
             */
            Locale: function (arg) {
                var that = this;
                that.scope = arg.scope;
                that.scope.lang = LocaleService.getLocaleData().code;
                that.scope.locale = $locale;
                that.scope.currencySymbol = LocaleService.getCurrencySymbol($locale.id);
                that.scope.$bus.subscribe('$translateChangeSuccess', function (data) {
                    that.scope.lang = Util.goodMapValue(data, "lang", LocaleService.getLocaleData().code);
                    that.scope.locale = $locale;
                    that.scope.currencySymbol = LocaleService.getCurrencySymbol(data.lang);
                    if (arg.onTranslateChangeSuccess) {
                        return arg.onTranslateChangeSuccess(data);
                    }
                });
            }
        };


        return Service;
    }
]);
