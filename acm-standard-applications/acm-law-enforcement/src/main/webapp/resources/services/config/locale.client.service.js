'use strict';

/**
 * @ngdoc service
 * @name services.service:Config.LocaleService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/config/locale.client.service.js services/config/locale.client.service.js}

 * LocaleService contains functions relate to locale.
 */
angular.module('services').config(function ($provide) {
    $provide.decorator('$translate', function ($delegate) {
        $delegate.dataLookups = {};


        /**
         * @ngdoc method
         * @name buildDataLookups
         * @methodOf services.service:$translate
         *
         * @description
         * Build a reverse translation table. Data resource key must end with "%".
         *    "bar.foo.greeting%": "Hello There"
         * becomes
         *    "bar.foo.Hello There": "bar.foo.greeting%"
         *
         * @param {Object} getLabelResources  Promise of retrieving label resources
         */
        $delegate.buildDataLookups = function(getLabelResources) {
            $delegate.dataLookups = {};

            getLabelResources.then(function(resourceParts) {
                _.each(resourceParts, function(resourcePart, langNs) {
                    var translationMap = resourcePart;

                    if (translationMap) {
                        var picked = _.pick (translationMap, function(value, key) {
                            return _.endsWith(key, "%");
                        });
                        _.each(picked, function(value, key) {
                            var lastIndex = key.lastIndexOf(".");
                            var category = key.substring(0, lastIndex);
                            $delegate.dataLookups[category + "." + value] = key;
                        });
                    }
                });

            });
        };

        /**
         * @ngdoc method
         * @name data
         * @methodOf services.service:$translate
         *
         * @description
         * Translate data into the language of current locale
         *
         * @param {String} data  Data value to be translated
         * @param {String} category  Resource key prefix. The category of key "bar.foo.key%" is "bar.foo"
         * @param {Object} interpolateParams  Same as in function $translate.instant()
         * @param {String} interpolationId  Same as in function $translate.instant()
         * @param {String} forceLanguage  Same as in function $translate.instant()
         * @param {String} sanitizeStrategy  Same as in function $translate.instant()
         *
         * @returns {String} Translated data
         */
        $delegate.data = function(data, category, interpolateParams, interpolationId, forceLanguage, sanitizeStrategy) {
            data = data.trim();
            var key = (category)? category + "." + data : data;
            var translationId = $delegate.dataLookups[key];
            if (translationId) {
                return $delegate.instant(translationId, interpolateParams, interpolationId, forceLanguage, sanitizeStrategy);
            } else {
                return data;
            }
        };

        /**
         * @ngdoc method
         * @name getKey
         * @methodOf services.service:$translate
         *
         * @description
         * Return translation ID, or key, of a data translation
         *
         * @param {String} data  Data value to be translated
         * @param {String} category  Resource key prefix. The category of key "bar.foo.key%" is "bar.foo"
         *
         * @returns {String} Resource key
         */
        $delegate.getKey = function(data, category) {
            data = data.trim();
            var key = (category)? category + "." + data : data;
            return $delegate.dataLookups[key];
        };

        return $delegate;
    });

}).factory('Config.LocaleService', ['$resource', 'Acm.StoreService', 'UtilService'
    , 'LookupService', '$translate', 'tmhDynamicLocale'
    , function ($resource, Store, Util
        , LookupService, $translate, dynamicLocale
    ) {
        var Service = $resource('api/latest/plugin', {}, {
            _getLabelResource: {
                url: "api/latest/plugin/admin/labelmanagement/resource?ns=:part&lang=:lang"
                    , method: "GET"
                    , cache: false
            },
            _getLabelResources: {
                url: "api/latest/plugin/admin/labelmanagement/resources?ns[]=:parts&lang=:lang"
                , method: "GET"
                , isArray: true
                , cache: false
            }
        });

        Service.SessionCacheNames = {
            LABEL_RESOURCE: "AcmLabelResource"
        };

        /**
         * @ngdoc method
         * @name getLabelResource
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Get label resources of given part and language
         *
         * @param {String} part  Resource part. Often it is an ArkCase module name
         * @param {String} lang  Language ID (Locale code).
         *
         * @returns {Object} Promise
         */
        Service.getLabelResource = function (part, lang) {
            var cacheLabelResource = new Store.SessionData(Service.SessionCacheNames.LABEL_RESOURCE);
            var labelResource = Util.goodValue(cacheLabelResource.get(), {});
            var labelResourcePartLang = labelResource[part + "." + lang];
            return Util.serviceCall({
                service: Service._getLabelResource
                , param: {part: part, lang: lang}
                , result: labelResourcePartLang
                , onSuccess: function (data) {
                    if (Service.validateLabelResource(data)) {
                        labelResourcePartLang = data;
                        labelResource[part + "." + lang] = labelResourcePartLang;
                        cacheLabelResource.set(labelResource);
                        return labelResourcePartLang;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name getLabelResources
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Get label resources of given part and language
         *
         * @param {Array} parts  String array of resource parts. Often there are ArkCase module names
         * @param {String} lang  Language ID (Locale code).
         *
         * @returns {Object} Promise
         */
        Service.getLabelResources = function (parts, lang) {
            if (!Util.isArray(parts)) {
                return Util.rejectPromise(null);
            }

            var cacheLabelResource = new Store.SessionData(Service.SessionCacheNames.LABEL_RESOURCE);
            var labelResource = Util.goodValue(cacheLabelResource.get(), {});
            var labelResourceParts = {};
            var partsNotFound = [];
            _.each(parts, function (part) {
                var res = labelResource[part + "." + lang];
                if (Util.isEmpty(res)) {
                    partsNotFound.push(part);
                } else {
                    labelResourceParts[part + "." + lang] = res;
                }
            });

            if (0 >= partsNotFound.length) {   //all parts found in cache; no need go further
                return Util.resolvePromise(labelResourceParts);
            }

            return Util.serviceCall({
                service: Service._getLabelResources
                , param: {"parts": partsNotFound, lang: lang}
                , onSuccess: function (data) {
                    if (Service.validateLabelResources(data)) {
                        _.each(data, function(resource){
                            var langData = Util.goodMapValue(resource, "lang", false);
                            var nsData = Util.goodMapValue(resource, "ns", false);
                            var resData = Util.goodMapValue(resource, "res", {});
                            if (langData && nsData) {
                                labelResourceParts[nsData + "." + langData] = resData;
                                labelResource[nsData + "." + langData] = resData;
                            }
                        });

                        cacheLabelResource.set(labelResource);
                        return labelResourceParts;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateLabelResource
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Validate label resource data.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateLabelResource = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateLabelResources
         * @methodOf services.service:Config.LocaleService
         *
         * @description
         * Validate multiple label resource parts data.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateLabelResources = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            if (0 < data.length) {
                if (Util.isEmpty(data[0].res)) {
                    return false;
                }
            }
            return true;
        };

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
            useLocale(localeData.code);
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
        function useLocale(localeCode) {
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