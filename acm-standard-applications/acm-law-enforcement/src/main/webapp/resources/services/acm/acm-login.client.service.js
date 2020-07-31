'use strict';

/**
 * @ngdoc service
 * @name services:Acm.LoginService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/acm/login.client.service.js services/acm/login.client.service.js}
 *
 * This service is used to manage login and logout. It holds login information, for example login status, idle time, current login ID, error statistics, etc.
 */

angular.module('services').factory('Acm.LoginService', [ '$q', '$state', '$injector', '$log', '$http', 'Acm.StoreService', 'UtilService', 'ConfigService', 'Analytics', 'WebSocketsListener', function($q, $state, $injector, $log, $http, Store, Util, ConfigService, Analytics, WebSocketService) {
    var Service = {
        LocalCacheNames: {
            LOGIN_INFO: "AcmLoginInfo",
            PENTAHO_LOGIN_INFO: "PentahoLoginInfo"
        }

        /**
         * @ngdoc method
         * @name getSetLoginPromise
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Return a promise to wait for setLogin(true) is called. If it is already login, it return true to
         * indicate the promise is already been resolved to true.
         */
        ,
        getSetLoginPromise: function() {
            var login = Service.isLogin();
            if (login) {
                return true;
            }

            return Service._deferSetLogin.promise;
        }

        ,
        _getLoginInfoCacheInstance: function() {
            var cacheLoginInfo = new Store.LocalData({
                name: Service.LocalCacheNames.LOGIN_INFO,
                noOwner: true,
                noRegistry: true
            });
            return cacheLoginInfo;
        }

        ,
        _getPentahoLoginInfoCacheInstance: function() {
            var pentahoCacheLoginInfo = new Store.LocalData({
                name: Service.LocalCacheNames.PENTAHO_LOGIN_INFO,
                noOwner: true,
                noRegistry: true
            });
            return pentahoCacheLoginInfo;
        }

        /**
         * @ngdoc method
         * @name isLogin
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Return boolean to indicate if current session is in login status
         */
        ,
        isLogin: function() {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = cacheLoginInfo.get();
            return Util.goodMapValue(loginInfo, "login", false);
        }

        /**
         * @ngdoc method
         * @name setLogin
         * @methodOf services:Acm.LoginService
         *
         * @param {Boolean} login Login status as boolean. true if login; false if not
         *
         * @description
         * Set login status
         */
        ,
        setLogin: function(login) {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = Util.goodValue(cacheLoginInfo.get(), {});
            loginInfo.login = login;
            cacheLoginInfo.set(loginInfo);
            if (login) {
                this._deferSetLogin.resolve(login);
            }
        }

        /**
         * @ngdoc method
         * @name getLastIdle
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Get last time marked as the beginning of an idle period
         */
        ,
        getLastIdle: function() {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = Util.goodValue(cacheLoginInfo.get(), {});
            return loginInfo.lastIdle = Util.goodMapValue(loginInfo, "lastIdle", new Date().getTime());
        }

        /**
         * @ngdoc method
         * @name setLastIdle
         * @methodOf services:Acm.LoginService
         *
         * @param {Boolean} (optional)val Last idle time in seconds. If not specified, current time is used
         *
         * @description
         * Set last user active time to mark as beginning of an idle period.
         */
        ,
        setLastIdle: function(val) {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = Util.goodValue(cacheLoginInfo.get(), {});
            loginInfo.lastIdle = Util.goodValue(val, new Date().getTime());
            cacheLoginInfo.set(loginInfo);
        }

        /**
         * @ngdoc method
         * @name getSinceIdle
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Set time elapses since beginning of idle
         */
        ,
        getSinceIdle: function() {
            var last = this.getLastIdle();
            var now = new Date().getTime();
            return now - last;
        }

        /**
         * @ngdoc method
         * @name getUserId
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Return current user ID
         */
        ,
        getUserId: function() {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = cacheLoginInfo.get();
            return Util.goodMapValue(loginInfo, "userId");
        }

        /**
         * @ngdoc method
         * @name setUserId
         * @methodOf services:Acm.LoginService
         *
         * @param {String} user Current user ID
         *
         * @description
         * Set current user ID
         */
        ,
        setUserId: function(userId) {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = Util.goodValue(cacheLoginInfo.get(), {});
            loginInfo.userId = userId;
            cacheLoginInfo.set(loginInfo);

            // set user id sent to Google Analytics
            Analytics.set('dimension1', userId);
        }

        /**
         * @ngdoc method
         * @name isConfirmCanceled
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Return true if user cancels a confirmation dialog in any window
         */
        ,
        isConfirmCanceled: function() {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = cacheLoginInfo.get();
            return Util.goodMapValue(loginInfo, "confirmCanceled", false);
        }

        /**
         * @ngdoc method
         * @name setConfirmCanceled
         * @methodOf services:Acm.LoginService
         *
         * @param {Boolean} confirmCanceled 'true' value indicates confirmation dialog is canceled
         *
         * @description
         * Set confirmCanceled flag
         */
        ,
        setConfirmCanceled: function(confirmCanceled) {
            var cacheLoginInfo = this._getLoginInfoCacheInstance();
            var loginInfo = Util.goodValue(cacheLoginInfo.get(), {});
            loginInfo.confirmCanceled = confirmCanceled;
            cacheLoginInfo.set(loginInfo);
        }

        ///**
        // * @ngdoc method
        // * @name resetCaches
        // * @methodOf services:Acm.LoginService
        // *
        // * @description
        // * Reset caches to get ready for new user or for next user
        // */
        //, resetCaches: function () {
        //    ConfigService.getModuleConfig("common").then(function (moduleConfig) {
        //        var resetCacheNames = Util.goodMapValue(moduleConfig, "resetCacheNames", []);
        //        _.each(resetCacheNames, function(cacheList) {
        //            var type = Util.goodMapValue(cacheList, "type");
        //            var names = Util.goodMapValue(cacheList, "names");
        //            if ("session" == Util.goodMapValue(cacheList, "type")) {
        //                try {
        //                    var service = $injector.get(Util.goodMapValue(cacheList, "service"));
        //                    var SessionCacheNames = Util.goodMapValue(service, Util.goodMapValue(cacheList, "names"), {});
        //                    _.each(SessionCacheNames, function (name) {
        //                        var cache = new Store.SessionData(name);
        //                        cache.set(null);
        //                    });
        //                } catch(e) {
        //                    $log.error("AcmLoginService: " + err.message);
        //                }
        //            }
        //        });
        //
        //        return moduleConfig;
        //    });
        //
        //    //Above ConfigService.getModuleConfig() just created a cache, reset it as well
        //    var cache = new Store.SessionData(ConfigService.SessionCacheNames.MODULE_CONFIG_MAP);
        //    cache.set(null);
        //}

        ,
        setIsLoggedIntoPentaho: function (isLoggedIntoPentaho) {
            var cacheLoginInfo = this._getPentahoLoginInfoCacheInstance();
            var loginInfo = Util.goodValue(cacheLoginInfo.get(), {});
            loginInfo.isLoggedIntoPentaho = isLoggedIntoPentaho;
            cacheLoginInfo.set(loginInfo);
        }

        ,
        getIsLoggedIntoPentaho: function () {
            var cacheLoginInfo = this._getPentahoLoginInfoCacheInstance();
            var loginInfo = cacheLoginInfo.get();
            return Util.goodMapValue(loginInfo, "isLoggedIntoPentaho", false);
        }

        /**
         * @ngdoc method
         * @name logoutAndCleanCaches
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Set off logout. Currently, it route to 'goodbye' page
         */
        ,
        logoutAndCleanCaches: function () {

            // "goodbye" page does the same cleaning, but may not be reliable. If some exception thrown (we saw real
            // practical example:
            // GET https://localhost:8843/arkcase/api/latest/plugin/admin/labelmanagement/resource?ns=goodbye&lang=en
            // returns 401 Unauthorized status),
            // "goodbye" page is not called. Call the benign cleanup here to make sure
            Service.setLogin(false);
            sessionStorage.clear();
            Store.Registry.clearSessionCache();
            Store.Registry.clearLocalCache();

            try {
                // disconnect websocket
                WebSocketService.disconnect();
            } catch (exc) {

            }

            $state.go("goodbye");
        }

        /**
         * @ngdoc method
         * @name logout
         * @methodOf services:Acm.LoginService
         *
         * @description
         * Handles ArkCase and Pentaho logout.  If the user is logged into Pentaho in the Adhoc Reports module
         * then the user needs to be logged out of Pentaho before starting the ArkCase logout.
         */
        ,
        logout: function() {
            if (Service.getIsLoggedIntoPentaho()) {
                // AFDP-9211: Need to logout of Pentaho before ArkCase session ends
                return $http({
                    method: 'GET',
                    url: 'pentaho/Logout'
                }).then(function (data) {
                    // AFDP-9211: Need to wait until Pentaho logout completed before logging out of ArkCase to
                    // prevent the Pentaho logout call from being interrupted when the ArkCase session is closed
                    Service.setIsLoggedIntoPentaho(false);
                    Service.logoutAndCleanCaches();
                }, function (error) {
                    Service.logoutAndCleanCaches();
                });
            } else {
                Service.logoutAndCleanCaches();
            }
        }
    };

    Service._deferSetLogin = $q.defer();

    return Service;
} ]);
