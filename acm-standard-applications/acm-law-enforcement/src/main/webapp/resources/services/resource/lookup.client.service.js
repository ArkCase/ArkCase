'use strict';

/**
 * @ngdoc service
 * @name services.service:LookupService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/resource/call-lookup.client.service.js services/resource/call-lookup.client.service.js}

 * LookupService contains functions to lookup data (typically static data).
 */
angular.module('services').factory('LookupService', ['$resource', 'Acm.StoreService', 'UtilService', 'SearchService'
    , function ($resource, Store, Util, SearchService) {
        var Service = $resource('api/latest/plugin', {}, {

            _getConfig: {
                url: "api/latest/service/config/:name"
                , method: "GET"
                , cache: false
            }
            , _getLookup: {
                url: "api/latest/service/config/:name"
                , method: "GET"
                , isArray: true
                , cache: false
            }
            , _saveLookup: {
                url: "api/latest/service/config/lookups"
                , method: "POST"
                , headers: {
                    "Content-Type": "application/json"
                }
            }

            /**
             * @ngdoc method
             * @name _getUsers
             * @methodOf services.service:LookupService
             *
             * @description
             * Query list of users
             *
             * @returns {Object} An object returned by $resource
             */
            , _getUsers: {
                url: "api/latest/plugin/search/advanced/USER/all"
                , method: "GET"
                , isArray: true
                , cache: false
            }

            /**
             * @ngdoc method
             * @name _getUsersBasic
             * @methodOf services.service:LookupService
             *
             * @description
             * Query users from SOLR
             *
             * @returns {Object} An object returned by $resource
             */
            , _getUsersBasic: {
                url: "api/latest/plugin/search/USER?n=1000&s=name asc"
                , method: "GET"
                , cache: false
            }

        });


        Service.SessionCacheNames = {
            USERS: "AcmUsers"
            , USERS_BASIC: "AcmUsersBasic"
            , USER_FULL_NAMES: "AcmUserFullNames"
            , CONFIG_MAP: "AcmConfigMap"
        };
        Service.CacheNames = {};


        /**
         * @ngdoc method
         * @name getUsers
         * @methodOf services.service:LookupService
         *
         * @description
         * Query list of users
         *
         * @returns {Object} Promise
         */
        Service.getUsers = function () {
            var cacheUsers = new Store.SessionData(Service.SessionCacheNames.USERS);
            var users = cacheUsers.get();
            return Util.serviceCall({
                service: Service._getUsers
                , result: users
                , onSuccess: function (data) {
                    if (Service.validateUsersRaw(data)) {
                        users = [];
                        _.each(data, function (item) {
                            users.push(Util.goodJsonObj(item));
                        });
                        cacheUsers.set(users);
                        return users;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name getUserFullNames
         * @methodOf services.service:LookupService
         *
         * @description
         * Query list of user full names
         *
         * @returns {Object} Promise
         */
        Service.getUserFullNames = function () {
            var cacheUserFullNames = new Store.SessionData(Service.SessionCacheNames.USER_FULL_NAMES);
            var userFullNames = cacheUserFullNames.get();
            return Util.serviceCall({
                service: Service._getUsers
                , result: userFullNames
                , onSuccess: function (data) {
                    if (Service.validateUsersRaw(data)) {
                        userFullNames = [];
                        var arr = data;
                        for (var i = 0; i < arr.length; i++) {
                            var obj = Util.goodJsonObj(arr[i]);
                            if (obj) {
                                var user = {};
                                user.id = Util.goodValue(obj.object_id_s);
                                user.name = Util.goodValue(obj.name);
                                userFullNames.push(user);
                            }
                        }
                        cacheUserFullNames.set(userFullNames);
                        return userFullNames;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateUsersRaw
         * @methodOf services.service:LookupService
         *
         * @description
         * Validate user list data in with each entry as stringify'ed JSON.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateUsersRaw = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getUsersBasic
         * @methodOf services.service:LookupService
         *
         * @description
         * Query list of users
         *
         * @returns {Object} Promise
         */
        Service.getUsersBasic = function () {
            var cacheUsers = new Store.SessionData(Service.SessionCacheNames.USERS_BASIC);
            var users = cacheUsers.get();
            return Util.serviceCall({
                service: Service._getUsersBasic
                , result: users
                , onSuccess: function (data) {
                    if (Service.validateUsersBasic(data)) {
                        users = [];
                        _.each(data.response.docs, function (doc) {
                            var user = {};
                            user.id = Util.goodValue(doc.object_id_s, 0);
                            user.name = Util.goodValue(doc.name);
                            users.push(user);

                        });
                        cacheUsers.set(users);
                        return users;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateUsersBasic
         * @methodOf services.service:LookupService
         *
         * @description
         * Validate users data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateUsersBasic = function (data) {
            if (!SearchService.validateSolrData(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name getConfig
         * @methodOf services.service:LookupService
         *
         * @description
         * Query a configuration
         *
         * @param {String} name  Config name
         *
         * @param {Array} arrBlackListPropertyNames if provided an array with property names to be omitted
         *
         * @returns {Object} Promise
         */
        Service.getConfig = function (name, arrBlackListPropertyNames) {
            var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
            var configMap = cacheConfigMap.get();
            var config = Util.goodMapValue(configMap, name, null);
            return Util.serviceCall({
                service: Service._getConfig
                , param: {name: name}
                , result: config
                , onSuccess: function (data) {
                    if (Service.validateConfig(data, name)) {
                        config = Util.omitNg(data);
                        if (arrBlackListPropertyNames) {
                            config = Util.deepOmit(data, arrBlackListPropertyNames);
                        }
                        configMap = configMap || {};
                        configMap[name] = config;
                        cacheConfigMap.set(configMap);
                        return config;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateConfig
         * @methodOf services.service:LookupService
         *
         * @description
         * Validate config data
         *
         * @param {Object} data  Data to be validated
         * @param {String} name  Data name
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateConfig = function (data, name) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name getLookup
         * @methodOf services.service:LookupService
         *
         * @description
         * Query a configuration with array as result
         *
         * @param {String} name  Config name
         *
         * @returns {Object} Promise
         */
        Service.getLookup = function (name) {
            var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
            var configMap = cacheConfigMap.get();
            var config = Util.goodMapValue(configMap, name, null);
            return Util.serviceCall({
                service: Service._getLookup
                , param: {name: name}
                , result: config
                , onSuccess: function (data) {
                    if (Service.validateLookup(data, name)) {
                        config = Util.omitNg(data);
                        configMap = configMap || {};
                        configMap[name] = config;
                        cacheConfigMap.set(configMap);
                        return config;
                    }
                }
            });
        };
        
        Service.getLookups = function() {
            var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
            var configMap = cacheConfigMap.get();
            var lookups = Util.goodMapValue(configMap, 'lookups', null);
            return Util.serviceCall({
                service: Service._getConfig
                , param: {name: 'lookups'}
                , result: lookups
                , onSuccess: function (data) {
                    lookups = Util.omitNg(data);
                    if (Service.validateLookups(lookups)) {                        
                        configMap = configMap || {};
                        configMap['lookups'] = lookups;
                        cacheConfigMap.set(configMap);
                        return lookups;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateLookup
         * @methodOf services.service:LookupService
         *
         * @description
         * Validate lookup data
         *
         * @param {Object} data  Data to be validated
         * @param {String} name  Data name
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateLookup = function (data, name) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };
        
        Service.validateLookups = function (data) {
            // check if the data contains only known lookup types
            for (var prop in data) {
                if (prop !== 'standardLookup' && prop !== 'subLookup' && prop !== 'inverseValuesLookup') {
                    return false;
                }
            }
            
            // check if the lookups are array objects
            if (data.standardLookup) {
                for (var i = 0; i < data.standardLookup.length; i++) {
                    if (!Util.isArray(data.standardLookup[i][Object.keys(data.standardLookup[i])[0]])) {
                        return false;
                    }
                }
            }
            if (data.subLookup) {
                for (var i = 0; i < data.subLookup.length; i++) {
                    if (!Util.isArray(data.subLookup[i][Object.keys(data.subLookup[i])[0]])) {
                        return false;
                    }
                }
            }
            if (data.inverseValuesLookup) {
                for (var i = 0; i < data.inverseValuesLookup.length; i++) {
                    if (!Util.isArray(data.inverseValuesLookup[i][Object.keys(data.inverseValuesLookup[i])[0]])) {
                        return false;
                    }
                }
            }
            return true;
        };

        Service.saveLookup = function (lookupDef, lookup) {
            lookupDef.lookupEntriesAsJson = JSON.stringify(lookup);
            return Util.serviceCall({
                service: Service._saveLookup
                , data: lookupDef
                , onSuccess: handleSaveLookupSuccess
            });
        };
        
        function handleSaveLookupSuccess(responseLookups) {
            var lookups = Util.omitNg(responseLookups);
            if (Service.validateLookups(lookups)) {
                var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
                var configMap = cacheConfigMap.get();
                configMap = configMap || {};
                configMap['lookups'] = lookups;
                cacheConfigMap.set(configMap);
                return lookups;
            }
        };
        
        return Service;
    }
]);