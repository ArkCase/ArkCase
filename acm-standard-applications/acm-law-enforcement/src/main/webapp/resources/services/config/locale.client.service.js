'use strict';

/**
 * @ngdoc service
 * @name services.service:Config.LocaleService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/config/locale.client.service.js services/config/locale.client.service.js}

 * LocaleService contains functions relate to locale.
 */
angular.module('services').factory('Config.LocaleService', ['$resource', 'Acm.StoreService', 'UtilService', 'LookupService'
    , function ($resource, Store, Util, LookupService) {
        var Service = $resource('api/latest/plugin', {}, {
        });

        Service.DEFAULT_SETTINGS = {"locales":[{"locale": "en", "desc": "English"}], "defaultLocale":"en"};

        /**
         * @ngdoc method
         * @name getSettings
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Retrieve locale settings.
         *
         * @returns {Object} Promise
         */
        Service.getSettings = function () {
            return Util.serviceCall({
                service: LookupService._getConfig
                , param: {name: "localeSettings"}
                , onSuccess: function (data) {
                    if (Service.validateSettings(data)) {
                        var localeSettings = data;
                        return localeSettings;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateSettings
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Validate locale setting data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSettings = function (data) {
            if (!data) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);