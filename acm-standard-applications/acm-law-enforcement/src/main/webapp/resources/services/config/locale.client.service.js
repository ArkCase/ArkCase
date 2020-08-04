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
angular.module('services').config(function($provide) {
    $provide.decorator('$translate', function($delegate) {
        $delegate.dataDict = {};

        /**
         * @ngdoc method
         * @name resetDataDict
         * @methodOf services.service:$translate
         *
         * @description
         * Reset the reverse translation table
         *
         * @returns {Object} Return to itself for chain'able function calls.
         */
        $delegate.resetDataDict = function(getLabelResources) {
            $delegate.dataDict = {};
            return $delegate;
        };

        /**
         * @ngdoc method
         * @name addDataDictFromLabels
         * @methodOf services.service:$translate
         *
         * @description
         * Add entries from label resources to the reverse translation table. Data resource key must end with "%".
         *    "bar.foo.greeting%": "Hello There"
         * becomes
         *    "bar.foo.Hello There": "bar.foo.greeting%"
         *
         * @param {Object} getLabelResources  Promise of retrieving label resources
         *
         * @returns {Object} Return to itself for chain'able function calls.
         */
        $delegate.addDataDictFromLabels = function(getLabelResources) {
            getLabelResources.then(function(resourceParts) {
                _.each(resourceParts, function(resourcePart, langNs) {
                    var translationMap = resourcePart;

                    if (translationMap) {
                        var picked = _.pick(translationMap, function(value, key) {
                            return _.endsWith(key, "%");
                        });
                        _.each(picked, function(value, key) {
                            var lastIndex = key.lastIndexOf(".");
                            var category = key.substring(0, lastIndex);
                            $delegate.dataDict[category + "." + value] = key;
                        });
                    }
                });

            });
            return $delegate;
        };

        /**
         * @ngdoc method
         * @name addDataDictFromLookup
         * @methodOf services.service:$translate
         *
         * @description
         * Add entries from key-value pair list to the reverse translation table. Example of a list:
         * var keyValueList = [{"key": "bar.foo.hello", "value": "Hello There"},
         *                     {"key": "bar.foo.bye",   "value": "Good Bye"}];
         *
         * @param {Object} keyValueList  List of key-value pair list. It can be a promise that will return such list
         * @param {String} (Optional)keyName  Key name of the key-value pair. Default value is "key"
         * @param {String} (Optional)valueName  Value name of the key-value pair. Default value is "value"
         *
         * @returns {Object} Return to itself for chain'able function calls.
         */
        $delegate.addDataDictFromLookup = function(keyValueList, keyName, valueName) {
            var keyName = keyName || "key";
            var valueName = valueName || "value";

            var addDataDictFromList = function(list) {
                _.each(list, function(keyValue) {
                    var value = keyValue[keyName];
                    var key = keyValue[valueName];
                    if (key && value) {
                        var lastIndex = key.lastIndexOf(".");
                        var category = key.substring(0, lastIndex);
                        $delegate.dataDict[category + "." + value] = key;
                    }
                });
            };

            if (_.isArray(keyValueList)) {
                addDataDictFromList(keyValueList);

            } else if (keyValueList.then) {
                keyValueList.then(function(list) {
                    addDataDictFromList(list);
                });
            }

            return $delegate;
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
            if (!data) {
                return "";
            }

            data = data.trim();
            var key = (category) ? category + "." + data : data;
            var translationId = $delegate.dataDict[key];
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
            var key = (category) ? category + "." + data : data;
            return $delegate.dataDict[key];
        };

        return $delegate;
    });

}).factory('Config.LocaleService', [ '$resource', '$locale', '$translate', 'tmhDynamicLocale', 'Acm.StoreService', 'UtilService', 'LookupService', function($resource, $locale, $translate, dynamicLocale, Store, Util, LookupService) {
    var Service = $resource('api/latest/plugin', {}, {
        _getLabelResource: {
            url: "api/latest/plugin/admin/labelmanagement/resource?ns=:part&lang=:lang",
            method: "GET",
            cache: false
        },
        _getLabelResources: {
            url: "api/latest/plugin/admin/labelmanagement/resources?" + encodeURIComponent('ns[]') + "=:parts&lang=:lang",
            method: "GET",
            isArray: true,
            cache: false
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
    Service.getLabelResource = function(part, lang) {
        var cacheLabelResource = new Store.SessionData(Service.SessionCacheNames.LABEL_RESOURCE);
        var labelResource = Util.goodValue(cacheLabelResource.get(), {});
        var labelResourcePartLang = labelResource[part + "." + lang];
        return Util.serviceCall({
            service: Service._getLabelResource,
            param: {
                part: part,
                lang: lang
            },
            result: labelResourcePartLang,
            onSuccess: function(data) {
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
    Service.getLabelResources = function(parts, lang) {
        if (!Util.isArray(parts)) {
            return Util.rejectPromise(null);
        }

        var cacheLabelResource = new Store.SessionData(Service.SessionCacheNames.LABEL_RESOURCE);
        var labelResource = Util.goodValue(cacheLabelResource.get(), {});
        var labelResourceParts = {};
        var partsNotFound = [];
        _.each(parts, function(part) {
            var res = labelResource[part + "." + lang];
            if (Util.isEmpty(res)) {
                partsNotFound.push(part);
            } else {
                labelResourceParts[part + "." + lang] = res;
            }
        });

        if (0 >= partsNotFound.length) { //all parts found in cache; no need go further
            return Util.resolvePromise(labelResourceParts);
        }

        return Util.serviceCall({
            service: Service._getLabelResources,
            param: {
                "parts": partsNotFound,
                lang: lang
            },
            onSuccess: function(data) {
                if (Service.validateLabelResources(data)) {
                    _.each(data, function(resource) {
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
    Service.validateLabelResource = function(data) {
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
    Service.validateLabelResources = function(data) {
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

    Service.DEFAULT_LOCALES = [ {
        "code": "en",
        "iso": "en",
        "desc": "English",
        "native": "English",
        "currencySymbol": "$"
    } ];
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
    Service.getSettings = function() {
        return doGetSettings(false);
    };

    /**
     * @ngdoc method
     * @name getLatestSettings
     * @methodOf services.service:Config.LocaleService
     *
     * @description
     * Retrieve locale settings, similar to getSettings(), but ignore cached settings.
     *
     * @returns {Object} Promise
     */
    Service.getLatestSettings = function() {
        return doGetSettings(true);
    };

    var doGetSettings = function(noCache) {
        var cacheLocale = new Store.LocalData({
            name: "AcmLocale",
            noOwner: true,
            noRegistry: true
        });
        var localeSettings = Util.goodValue(noCache, false) ? null : cacheLocale.get();

        return Util.serviceCall({
            service: LookupService._getConfig,
            param: {
                name: "localeSettings"
            },
            result: localeSettings,
            onSuccess: function(data) {
                if (Service.validateSettings(data)) {
                    var localeSettings = Service.getLocaleData();
                    localeSettings.locales = data.locales;
                    Service.setLocaleData(localeSettings);
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
    Service.validateSettings = function(data) {
        if (!data) {
            return false;
        }
        if (!Util.isArray(data.locales)) {
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
    Service.getLocaleData = function() {
        var cacheLocale = new Store.LocalData({
            name: "AcmLocale",
            noOwner: true,
            noRegistry: true
        });
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
    Service.setLocaleData = function(localeData) {
        if (Util.isEmpty(localeData)) {
            localeData = {};
            localeData.locales = Service.DEFAULT_LOCALES;
            localeData.code = Service.DEFAULT_CODE;
            localeData.iso = Service.DEFAULT_ISO;
        }
        var cacheLocale = new Store.LocalData({
            name: "AcmLocale",
            noOwner: true,
            noRegistry: true
        });
        cacheLocale.set(localeData);
    };

    /**
     * @ngdoc method
     * @name requestLocale
     * @methodOf services.service:Config.LocaleService
     *
     * @description
     * Request for given locale. User requested locale is usually honored unless LocaleService is not ready;
     * in which case, a default is used.
     *
     * This function only registers the selected locale in the service. Need to call useLocale() function
     * to actually make the locale change.
     *
     * @param {Object} localeData  Data to be cached
     *
     * @returns {Object} Locale adopted
     */
    Service.requestLocale = function(localeCode) {
        var localeData = Service.getLocaleData();

        var arkcaseRtl = document.getElementById("arkcase-rtl");
        var bootstrapRtl = document.getElementById("bootstrap-rtl");

        if (localeCode === "ar" && arkcaseRtl === null && bootstrapRtl === null) {
            arkcaseRtl = document.createElement("link");
            arkcaseRtl.setAttribute("rel", "stylesheet");
            arkcaseRtl.setAttribute("href", "assets/css/arkcase-rtl.css");
            arkcaseRtl.setAttribute("id", "arkcase-rtl");
            document.getElementsByTagName("head")[0].appendChild(arkcaseRtl);

            bootstrapRtl = document.createElement("link");
            bootstrapRtl.setAttribute("rel", "stylesheet");
            bootstrapRtl.setAttribute("href", "assets/css/bootstrap-rtl.css");
            bootstrapRtl.setAttribute("id", "bootstrap-rtl");
            document.getElementsByTagName("head")[0].appendChild(bootstrapRtl);
        }

        if (localeCode !== "ar") {
            if (arkcaseRtl && bootstrapRtl) {
                arkcaseRtl.remove();
                bootstrapRtl.remove();
            }
        }

        var locale = _.find(localeData.locales, {
            code: localeCode
        });
        if (!locale) {
            locale = _.find(Service.DEFAULT_LOCALES, {
                code: Service.DEFAULT_CODE
            });
        }

        localeData.code = localeCode;
        localeData.iso = locale.iso;
        Service.setLocaleData(localeData);

        return locale;
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
    Service.useLocale = function(localeCode) {
        $translate.use(localeCode);
        dynamicLocale.set(localeCode);
        //$locale.currencySymbol = Service.getCurrencySymbol(localeCode);
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
    Service.getCurrencySymbol = function(localeCode) {
        var locale = Service.findLocale(localeCode);
        return Util.goodMapValue(locale, "currencySymbol");
    };

    /**
     * @ngdoc method
     * @name getIso
     * @methodOf services.service:Config.LocaleService
     *
     * @description
     * Get current ISO locale code
     *
     * @param {String} (Optional)localeCode  Locale code. If not given, current locale code is used.
     *
     * @returns {String} Current ISO locale code
     */
    Service.getIso = function(localeCode) {
        var locale = Service.findLocale(localeCode);
        return Util.goodMapValue(locale, "iso");
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
    Service.findLocale = function(localeCode) {
        var localeData = Service.getLocaleData();
        var locales = Util.goodMapValue(localeData, "locales", Service.DEFAULT_LOCALES);
        if (Util.isEmpty(localeCode)) {
            localeCode = Util.goodMapValue(localeData, "code", Service.DEFAULT_CODE);
        }
        return _.find(locales, {
            code: localeCode
        });
    };

    return Service;
} ]);
