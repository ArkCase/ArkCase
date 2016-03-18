'use strict';

/**
 * @ngdoc service
 * @name services.service:LoginStatService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/login-stat.client.service.js services/acm/login-stat.client.service.js}
 *
 * This service is used to hold login information, for example login status, idle time, error statistics, etc.
 */

angular.module('services').factory('Acm.LoginStatService', ['StoreService', 'UtilService'
    , function (Store, Util) {
        var Service = {
            LocalCacheNames: {
                LOGIN_STATUS: "AcmLoginStatus"
            }

            /**
             * @ngdoc method
             * @name isLogin
             * @methodOf services.service:LoginStatService
             *
             * @description
             * Return boolean to indicate if current session is in login status
             */
            , isLogin: function () {
                var cacheLoginStatus = new Store.LocalData(Service.LocalCacheNames.LOGIN_STATUS);
                var loginStatus = cacheLoginStatus.get();
                return Util.goodValue(loginStatus, false);
            }

            /**
             * @ngdoc method
             * @name setLogin
             * @methodOf services.service:LoginStatService
             *
             * @param {Boolean} status Login status as boolean. true if login; false if logout
             *
             * @description
             * Set login status
             */
            , setLogin: function (status) {
                var cacheLoginStatus = new Store.LocalData(Service.LocalCacheNames.LOGIN_STATUS);
                cacheLoginStatus.set(status);
            }

        };

        return Service;
    }
]);