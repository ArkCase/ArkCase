'use strict';

/**
 * @ngdoc service
 * @name services.service:Authentication
 *
 * @description
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/auth/authentication.client.service.js services/auth/authentication.client.service.js}
 *
 * The Authentication service retrieves user information from server
 */
angular.module('services').factory('Authentication', [ '$resource', 'Acm.StoreService', 'UtilService', function($resource, Store, Util) {
    var Service = $resource('api/v1/users/info', {}, {
        /**
         * @ngdoc method
         * @name _queryUserInfo
         * @methodOf services.service:Authentication
         *
         * @description
         * Query current login user info
         *
         * @returns {Object} Returned by $resource
         */
        _queryUserInfo: {
            method: 'GET',
            url: 'api/v1/users/info'
        }

        ,
        _updateUserLang: {
            method: 'POST',
            url: 'api/v1/users/lang/:lang'
        },

        _getUserPrivileges: {
            method: 'GET',
            url: 'api/v1/users/userPrivileges',
            isArray: true
        }
    });

    Service.SessionCacheNames = {
        USER_INFO: "AcmUserInfo"
    };

    /**
     * @ngdoc method
     * @name queryUserInfo
     * @methodOf services.service:Authentication
     *
     * @description
     * Query current login user info
     *
     * @returns {Object} Promise
     */
    Service.queryUserInfo = function() {
        var cacheUserInfo = new Store.SessionData(Service.SessionCacheNames.USER_INFO);
        var userInfo = cacheUserInfo.get();
        return Util.serviceCall({
            service: Service._queryUserInfo,
            result: userInfo,
            onSuccess: function(data) {
                if (Service.validateUserInfo(data)) {
                    userInfo = data;
                    Store.fixOwner(userInfo.userId);
                    cacheUserInfo.set(userInfo);
                    return userInfo;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateUserInfo
     * @methodOf services.service:Authentication
     *
     * @description
     * Validate case data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateUserInfo = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (Util.isEmpty(data.userId)) {
            return false;
        }
        if (Util.isEmpty(data.fullName)) {
            return false;
        }
        if (Util.isEmpty(data.firstName)) {
            return false;
        }
        if (Util.isEmpty(data.lastName)) {
            return false;
        }
        return true;
    };

    Service.updateUserLang = function(lang) {
        return Util.serviceCall({
            service: Service._updateUserLang,
            param: {
                lang: lang
            },
            data: {},
            onSuccess: function(data) {
                if (Service.validateUpdateUserLang(data)) {
                    var cacheUserInfo = new Store.SessionData(Service.SessionCacheNames.USER_INFO);
                    var userInfo = cacheUserInfo.get();
                    if (!Util.isEmpty(userInfo)) {
                        userInfo.langCode = lang;
                        cacheUserInfo.set(userInfo);
                    }
                    return data;
                }
            }
        });
    };

    Service.getUserPrivileges = function(lang) {
        return Util.serviceCall({
            service: Service._getUserPrivileges,
            data: {},
            onSuccess: function(data) {
                return data
            }
        });
    };

    Service.validateUpdateUserLang = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);