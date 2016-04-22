'use strict';

/**
 * @ngdoc service
 * @name services:Acm.LoginStatService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/login-stat.client.service.js services/acm/login-stat.client.service.js}
 *
 * This service is used to hold login information, for example login status, idle time, error statistics, etc.
 */

angular.module('services').factory('Acm.LoginStatService', ['Acm.StoreService', 'UtilService'
    , function (Store, Util) {
        var Service = {
            LocalCacheNames: {
                LOGIN_STATUS: "AcmLoginStatus"
            }

            /**
             * @ngdoc method
             * @name isLogin
             * @methodOf services:Acm.LoginStatService
             *
             * @description
             * Return boolean to indicate if current session is in login status
             */
            , isLogin: function () {
                var cacheLoginStatus = new Store.LocalData(Service.LocalCacheNames.LOGIN_STATUS);
                var loginStatus = cacheLoginStatus.get();
                return Util.goodMapValue(loginStatus, "login", false);
            }

            /**
             * @ngdoc method
             * @name setLogin
             * @methodOf services:Acm.LoginStatService
             *
             * @param {Boolean} login Login status as boolean. true if login; false if not
             *
             * @description
             * Set login status
             */
            , setLogin: function (login) {
                var cacheLoginStatus = new Store.LocalData(Service.LocalCacheNames.LOGIN_STATUS);
                var loginStatus = Util.goodValue(cacheLoginStatus.get(), {});
                loginStatus.login = login;
                cacheLoginStatus.set(loginStatus);
            }

            /**
             * @ngdoc method
             * @name getLastIdle
             * @methodOf services:Acm.LoginStatService
             *
             * @description
             * Get last idle time
             */
            , getLastIdle: function () {
                var cacheLoginStatus = new Store.LocalData(Service.LocalCacheNames.LOGIN_STATUS);
                var loginStatus = Util.goodValue(cacheLoginStatus.get(), {});
                return loginStatus.lastIdle = Util.goodMapValue(loginStatus, "lastIdle", new Date().getTime());
            }

            /**
             * @ngdoc method
             * @name setLastIdle
             * @methodOf services:Acm.LoginStatService
             *
             * @param {Boolean} (optional)val Last idle time in seconds. If not specified, current time is used
             *
             * @description
             * Set last user active time to mark as beginning of an idle period.
             */
            , setLastIdle: function (val) {
                var cacheLoginStatus = new Store.LocalData(Service.LocalCacheNames.LOGIN_STATUS);
                var loginStatus = Util.goodValue(cacheLoginStatus.get(), {});
                loginStatus.lastIdle = Util.goodValue(val, new Date().getTime());
                cacheLoginStatus.set(loginStatus);
            }

            /**
             * @ngdoc method
             * @name getSinceIdle
             * @methodOf services:Acm.LoginStatService
             *
             * @description
             * Set time elapses since last idle
             */
            , getSinceIdle: function () {
                var last = this.getLastIdle();
                var now = new Date().getTime();
                return now - last;
            }

        };

        return Service;
    }
]);