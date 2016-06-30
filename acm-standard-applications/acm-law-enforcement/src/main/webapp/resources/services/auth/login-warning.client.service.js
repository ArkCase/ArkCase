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
angular.module('services').factory('LoginWarningService', ['$resource', 'Acm.StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('/warning', {}, {
            _queryLoginWarning: {
                method: 'GET',
                url: 'warning'
            }
        });


        Service.SessionCacheNames = {
            LoginWarning: "AcmLoginWarning"
        };

        /**
         * @ngdoc method
         * @name queryLoginWarning
         * @methodOf services.service:LoginWarning
         *
         * @description
         * Query login warning info
         *
         * @returns {Object} Promise
         */
        Service.queryLoginWarning = function () {
            var cacheUserInfo = new Store.SessionData(Service.SessionCacheNames.LoginWarning);
            var warningInfo = cacheUserInfo.get();
            return Util.serviceCall({
                service: Service._queryLoginWarning
                , result: warningInfo
                , onSuccess: function (data) {
                    if (Service.validateWarningInfo(data)) {
                        warningInfo = data;
                        cacheUserInfo.set(warningInfo);
                        return warningInfo;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateWarningInfo
         * @methodOf services.service:LoginWarning
         *
         * @description
         * Validate login warning data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateWarningInfo = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.enabled)) {
                return false;
            }
            return true;
        };
        return Service;
    }
]);