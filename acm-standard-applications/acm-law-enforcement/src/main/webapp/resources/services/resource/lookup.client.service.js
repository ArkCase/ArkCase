'use strict';

/**
 * @ngdoc service
 * @name services.service:LookupService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/resource/call-lookup.client.service.js services/resource/call-lookup.client.service.js}

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
         * @returns {Object} Promise
         */
        Service.getConfig = function (name) {
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

        return Service;
    }
]);