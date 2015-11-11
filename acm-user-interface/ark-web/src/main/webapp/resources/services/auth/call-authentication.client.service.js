'use strict';

/**
 * @ngdoc service
 * @name services.service:CallAuthentication
 *
 * @description
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/auth/call-authentication.client.service.js services/auth/call-authentication.client.service.js}
 *
 * The CallAuthentication is a wrapper of Authentication service
 */
angular.module('services').factory('CallAuthentication', ['StoreService', 'UtilService', 'ValidationService', 'Authentication',
    function (Store, Util, Validator, Authentication) {
        var ServiceCall = {
            SessionCacheNames: {
                USER_INFO: "AcmUserInfo"
            }

            /**
             * @ngdoc method
             * @name getCaseInfo
             * @methodOf services.service:CallAuthentication
             *
             * @description
             * Query current login user info
             *
             * @returns {Object} Promise
             */
            , queryUserInfo: function () {
                var cacheUserInfo = new Store.SessionData(this.SessionCacheNames.USER_INFO);
                var userInfo = cacheUserInfo.get();
                return Util.serviceCall({
                    service: Authentication.queryUserInfo
                    , result: userInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateUserInfo(data)) {
                            userInfo = data;
                            cacheUserInfo.set(userInfo);
                            return userInfo;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateUserInfo
             * @methodOf services.service:CallAuthentication
             *
             * @description
             * Validate case data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateUserInfo: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.userId)) {
                    return false;
                }
                if (Util.isEmpty(data.fullName)) {
                    return false;
                }
                if (Util.isEmpty(data.mail)) {
                    return false;
                }
                if (Util.isEmpty(data.firstName)) {
                    return false;
                }
                if (Util.isEmpty(data.lastName)) {
                    return false;
                }
                return true;
            }

        };

        return ServiceCall;
    }
]);