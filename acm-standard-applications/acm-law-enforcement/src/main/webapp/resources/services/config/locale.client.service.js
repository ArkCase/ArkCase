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
angular.module('services').factory('Config.LocaleService', ['$resource', 'Acm.StoreService', 'UtilService'
    , 'LookupService', '$translate', 'tmhDynamicLocale'
    , function ($resource, Store, Util
        , LookupService, $translate, dynamicLocale
    ) {
        var Service = $resource('api/latest/plugin', {}, {
        });

        Service.DEFAULT_LOCALES = [{"code": "en", "iso": "en", "desc": "English", "native": "English", "currencySymbol": "$"}];
        Service.DEFAULT_CODE = "en";
        Service.DEFAULT_ISO = "en";

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

        /**
         * @ngdoc method
         * @name getLocaleData
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Retrieve last locale data stored in session. If not found, a default one is used.
         *
         * @returns {Object} last locale stored
         */
        Service.getLocaleData = function () {
            var cacheLocale = new Store.LocalData({name: "AcmLocale", noOwner: true, noRegistry: true});
            var localeData = cacheLocale.get();
            if (Util.isEmpty(localeData)) {
                localeData = {};
                localeData.locales = Service.DEFAULT_LOCALES;
                localeData.code = Service.DEFAULT_CODE;
                localeData.iso = Service.DEFAULT_ISO;
                cacheLocale.set(localeData);
            }
            return localeData;
        };

        /**
         * @ngdoc method
         * @name setLocaleData
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Store given locale data to session for later use.
         *
         * @param {Object} localeData  Data to be cached
         */
        Service.setLocaleData = function (localeData) {
            if (Util.isEmpty(localeData)) {
                localeData = {};
                localeData.locales = Service.DEFAULT_LOCALES;
                localeData.code = Service.DEFAULT_CODE;
                localeData.iso = Service.DEFAULT_ISO;
            }
            var cacheLocale = new Store.LocalData({name: "AcmLocale", noOwner: true, noRegistry: true});
            cacheLocale.set(localeData);
        };

        /**
         * @ngdoc method
         * @name useLocale
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Switch current locale to given one.
         *
         * @param {String} localeCode  Locale code
         */
        Service.useLocale = function (localeCode) {
            $translate.use(localeCode);
            dynamicLocale.set(localeCode);
        };

        /**
         * @ngdoc method
         * @name getCurrencySymbol
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Get currency symbol of an application defined currency in a locale language. It is different from the
         * currency symbol of the locale.
         *
         * @param {String} (Optional)localeCode  Locale code. If not given, current locale code is used.
         *
         * @returns {String} Currency symbol of the currency
         */
        Service.getCurrencySymbol = function (localeCode) {
            var locale = Service.findLocale(localeCode);
            return Util.goodMapValue(locale, "currencySymbol");
        };

        /**
         * @ngdoc method
         * @name findLocale
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Search locale setting for the given locale code.
         *
         * @param {String} localeCode  Locale code. If not given, current locale code is used.
         *
         * @returns {Object} Locale found
         */
        Service.findLocale = function (localeCode) {
            var localeData = Service.getLocaleData();
            var locales = Util.goodMapValue(localeData, "locales", Service.DEFAULT_LOCALES);
            if (Util.isEmpty(localeCode)) {
                localeCode = Util.goodMapValue(localeData, "code", Service.DEFAULT_CODE);
            }
            return _.find(locales, {code: localeCode});
        };

        return Service;
    }
]);