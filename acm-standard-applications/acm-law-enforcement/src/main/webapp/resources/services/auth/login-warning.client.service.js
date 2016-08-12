'use strict';

/**
 * @ngdoc service
 * @name services.service:LoginWarningService
 *
 * @description
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/auth/login-warning.client.service.js services/auth/login-warning.client.service.js}
 *
 * The LoginWarningService service retrieves warning configuration for login from server
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
            , WarningAccepted: "warningAccepted"
        };

        /**
         * @ngdoc method
         * @name getWarningAccepted
         * @methodOf services.service:LoginWarning
         *
         * @description
         * Get warning accepted status
         *
         * @returns {boolean} Return warning accepted status
         */
        Service.getWarningAccepted = function () {
            var cacheWarningAccepted = new Store.SessionData(Service.SessionCacheNames.WarningAccepted);
            return cacheWarningAccepted.get();
        };

        /**
         * @ngdoc method
         * @name setWarningAccepted
         * @methodOf services.service:LoginWarning
         *
         * @param {boolean} warningAccepted  Warning accepted status
         *
         * @description
         * Set warning accepted status
         */
        Service.setWarningAccepted = function (warningAccepted) {
            var cacheWarningAccepted = new Store.SessionData(Service.SessionCacheNames.WarningAccepted);
            return cacheWarningAccepted.set(warningAccepted);
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
            var cacheLoginWarning = new Store.SessionData(Service.SessionCacheNames.LoginWarning);
            var warningInfo = cacheLoginWarning.get();
            return Util.serviceCall({
                service: Service._queryLoginWarning
                , result: warningInfo
                , onSuccess: function (data) {
                    if (Service.validateWarningInfo(data)) {
                        warningInfo = data;
                        cacheLoginWarning.set(warningInfo);
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