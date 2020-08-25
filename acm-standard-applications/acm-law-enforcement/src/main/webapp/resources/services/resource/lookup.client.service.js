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
angular.module('services').factory('LookupService', ['$resource', 'Acm.StoreService', 'UtilService', 'SearchService', 'MessageService', '$log', '$q', function ($resource, Store, Util, SearchService, MessageService, $log, $q) {
    var Service = $resource('api/latest/plugin', {}, {

        _getConfig: {
            url: "api/latest/service/config/:name",
            method: "GET",
            cache: false
        },
        _getLookups: {
            url: "api/latest/service/config/lookups",
            method: "GET",
            cache: false
        },
        _getLookup: {
            url: "api/latest/service/config/:name",
            method: "GET",
            isArray: true,
            cache: false
        },
        _saveLookup: {
            url: "api/latest/service/config/lookups",
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            }
        },
        _deleteLookup: {
            url: "api/latest/service/config/lookups/:name/:lookupType",
            method: "DELETE",
            cache: false
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
        ,
        _getUsers: {
            url: "api/latest/plugin/search/advanced/USER/all",
            method: "GET",
            isArray: true,
            cache: false
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
        ,
        _getUsersBasic: {
            url: "api/latest/plugin/search/USER?n=1000&s=name asc",
            method: "GET",
            cache: false
        },
        _deleteSubLookup: {
            url: "api/latest/service/config/lookups/:name/:parentName/sublookup",
            method: "POST",
            cache: false
        }

    });

    Service.SessionCacheNames = {

        USERS: "AcmUsers",
        USERS_BASIC: "AcmUsersBasic",
        USER_FULL_NAMES: "AcmUserFullNames",
        CONFIG_MAP: "AcmConfigMap"
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
    Service.getUsers = function() {
        var cacheUsers = new Store.SessionData(Service.SessionCacheNames.USERS);
        var users = cacheUsers.get();
        return Util.serviceCall({
            service: Service._getUsers,
            result: users,
            onSuccess: function(data) {
                if (Service.validateUsersRaw(data)) {
                    users = [];
                    _.each(data, function(item) {
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
    Service.getUserFullNames = function() {
        var cacheUserFullNames = new Store.SessionData(Service.SessionCacheNames.USER_FULL_NAMES);
        var userFullNames = cacheUserFullNames.get();
        return Util.serviceCall({
            service: Service._getUsers,
            result: userFullNames,
            onSuccess: function(data) {
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
     * @name getUserFullName
     * @methodOf services.service:LookupService
     *
     * @description
     * User full name
     *
     * @returns {String}
     */
    Service.getUserFullName = function (userName) {
        var foundUser = "";
        return Service.getUserFullNames().then(function (foundUsers) {
            angular.forEach(foundUsers, function (value, key) {
                if (userName === value.id) {
                    foundUser = value.name;
                }
            });

            if (Util.isEmpty(foundUser)) {
                return userName;
            } else {
                return foundUser;
            }

        });
    };

    /**
     * @ngdoc method
     * @name getApprovers
     * @methodOf services.service:LookupService
     *
     * @description
     * Query list of approvers full names and user id
     *
     * @returns {Object} approvers
     */

    Service.getApprovers = function(objectInfo) {
        return Service.getUserFullNames().then(function(users) {
            var approvers = [];
            if (!Util.isArrayEmpty(objectInfo.participants) && !Util.isArrayEmpty(users)) {
                for (var i = 0; i < objectInfo.participants.length; i++) {
                    for (var j = 0; j < users.length; j++) {
                        if (objectInfo.participants[i].participantLdapId === users[j].id && objectInfo.participants[i].participantType === 'approver') {
                            approvers.push({
                                fullName: users[j].name,
                                userId: users[j].id
                            });
                            break;
                        }
                    }
                }
            }
            return approvers;
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
    Service.validateUsersRaw = function(data) {
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
    Service.getUsersBasic = function() {
        var cacheUsers = new Store.SessionData(Service.SessionCacheNames.USERS_BASIC);
        var users = cacheUsers.get();
        return Util.serviceCall({
            service: Service._getUsersBasic,
            result: users,
            onSuccess: function(data) {
                if (Service.validateUsersBasic(data)) {
                    users = [];
                    _.each(data.response.docs, function(doc) {
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
    Service.validateUsersBasic = function(data) {
        if (!Util.validateSolrData(data)) {
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
    Service.getConfig = function(name, arrBlackListPropertyNames) {
        var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
        var configMap = cacheConfigMap.get();
        var config = Util.goodMapValue(configMap, name, null);
        return Util.serviceCall({
            service: Service._getConfig,
            param: {
                name: name
            },
            result: config,
            onSuccess: function(data) {
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
    Service.validateConfig = function(data, name) {
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
     * @deprecated Use getLookups() from lookup.client.service.js or getLookupByLookupName(name) from object-lookup.client.service.js
     */
    Service.getLookup = function(name) {
        $log.warn("Using deprecated function getLookup(name) from lookup.client.service.js!");
        var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
        var configMap = cacheConfigMap.get();
        var config = Util.goodMapValue(configMap, name, null);
        return Util.serviceCall({
            service: Service._getLookup,
            param: {
                name: name
            },
            result: config,
            onSuccess: function(data) {
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
     * @name getLookups
     * @methodOf services.service:LookupService
     *
     * @description
     * Query the server configuration for all lookups definitions.
     * Returns the lookup definitions as object with structure:
     * {
     *     "standardLookup" : [{
     *         "someStandardLookup" : [
     *                 { "key" : "1", "value" : "1"},
     *                 { "key" : "2", "value" : "2"}
     *             ]
     *         }, {
     *         "anotherStandardLookup" : [
     *                 { "key" : "1", "value" : "1" },
     *                 { "key" : "2", "value" : "2" }
     *             ]
     *         }
     *     ],
     *     "nestedLookup" : [{
     *         "contactMethodTypes" : [
     *                 { "key" : "1", "value" : "1", "subLookup" : [
     *                         { "key" : "11", "value" : "11" },
     *                         { "key" : "12", "value" : "12" },
     *                         { "key" : "13", "value" : "13" }
     *                     ]
     *                 },
     *                 { "key" : "2", "value" : "2", "subLookup" : [
     *                         { "key" : "21", "value" : "21" }
     *                     ]
     *                 }
     *             ]
     *         }
     *     ],
     *     "inverseValuesLookup" : [{
     *             "organizationRelationTypes" : [
     *                 { "key" : "1", "value" : "1", "inverseKey" : "inv1", "inverseValue" : "inv1" },
     *                 { "key" : "2", "value" : "2", "inverseKey" : "inv2", "inverseValue" : "inv2" }
     *             ]
     *         }
     *     ]
     * }
     *
     * @returns {Object} Promise
     */
    Service.getLookups = function() {
        var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
        var configMap = cacheConfigMap.get();
        var lookups = Util.goodMapValue(configMap, 'lookups', null);
        return Util.serviceCall({
            service: Service._getLookups,
            result: lookups,
            onSuccess: handleSaveLookupSuccess
        });
    };

    /**
     * @ngdoc method
     * @name deleteLookup
     * @methodOf services.service:LookupService
     *
     * @description
     * Delete lookup
     *
     * @returns {Object} Promise
     */
    Service.deleteLookup = function (lookupName, lookupType) {
        var cacheConfigMap = new Store.SessionData(Service.SessionCacheNames.CONFIG_MAP);
        var configMap = cacheConfigMap.get();
        var lookups = Util.goodMapValue(configMap, 'lookups', null);
        return Util.serviceCall({
            service: Service._deleteLookup,
            param: {
                name: lookupName,
                lookupType: lookupType
            },
            onSuccess: handleSaveLookupSuccess
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
    Service.validateLookup = function(data, name) {
        if (!Util.isArray(data)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name validateLookups
     * @methodOf services.service:LookupService
     *
     * @description
     * Validate lookups data
     *
     * @param {Object} data  Lookups data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateLookups = function(data) {
        // check if the data contains only known lookup types
        for ( var prop in data) {
            if (prop !== 'standardLookup' && prop !== 'nestedLookup' && prop !== 'inverseValuesLookup') {
                return false;
            }
        }

        // check if the lookups are array objects
        if (data.standardLookup) {
            angular.forEach(data.standardLookup, function(value, key) {
                if (!Util.isArray(value.entries)) {
                    return false;
                }
            });
        }
        if (data.nestedLookup) {
            angular.forEach(data.nestedLookup, function(value, key) {
                if (!Util.isArray(value.entries)) {
                    return false;
                }
            });
        }
        if (data.inverseValuesLookup) {
            angular.forEach(data.inverseValuesLookup, function(value, key) {
                if (!Util.isArray(value.entries)) {
                    return false;
                }
            });
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name saveLookup
     * @methodOf services.service:LookupService
     *
     * @description
     * Saves the the given lookup entries for the lookup definition.
     *
     * @param {Object} lookupDef    the lookup definition to be saved with structure:
     *                              { 'lookupType' : 'standardLookup', 'name' : 'addressTypes' }
     * @parma {Array}  lookup       the lookup entries as an array.
     *                              For standarLookup the structure looks like:
     *                              [{'key':'1', 'value':'1'}, {'key':'2', 'value':'2'}, {...}]
     *                              For nestedLookup the structure looks like:
     *                              [{'key':'1', 'value':'1', 'subLookup' : [{'key':'11', 'value':'11'}, {'key':'12', 'value':'12'}]}, {...}]
     *                              For inverseValuesLookup the structure looks like:
     *                              [{'key':'1', 'value':'1', 'inverseKey':'inv1', 'inverseValue':'inv1'}, {'key':'2', 'value':'2', 'inverseKey':'inv2', 'inverseValue':'inv2'}]
     *
     * @returns {Object} Promise returning all lookups from server.
     */
    Service.saveLookup = function(lookupDef, lookup) {
        lookupDef.lookupEntriesAsJson = JSON.stringify(lookup);
        return Util.serviceCall({
            service: Service._saveLookup,
            data: lookupDef,
            onSuccess: handleSaveLookupSuccess
        });
    };

    /**
     * @ngdoc method
     * @name deleteSubLookup
     * @methodOf services.service:LookupService
     *
     * @description
     * Delete sub lookup
     *
     * @param {String} subLookupName
     * @param {String} parentName
     * @param {Object} lookupDefinition
     *
     * @returns {Object} Promise
     */
    Service.deleteSubLookup = function (subLookupName, parentName, lookupDefinition) {
        lookupDefinition.lookupEntriesAsJson = "";
        lookupDefinition = JSON.parse(angular.toJson(lookupDefinition))
        return Util.serviceCall({
            service: Service._deleteSubLookup,
            param: {
                name: subLookupName,
                parentName: parentName
            },
            data: lookupDefinition,
            onSuccess: handleSaveLookupSuccess
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
        } else {
            // should not happen
            return MessageService.error('Lookups returned from server are invalid!');
        }
    }
    ;

    return Service;
} ]);