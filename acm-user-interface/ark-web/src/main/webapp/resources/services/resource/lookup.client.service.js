'use strict';

/**
 * @ngdoc service
 * @name services.service:LookupService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-lookup.client.service.js services/resource/call-lookup.client.service.js}

 * LookupService contains functions to lookup data (typically static data).
 */
angular.module('services').factory('LookupService', ['$resource', 'StoreService', 'UtilService', 'Solr.SearchService'
    , function ($resource, Store, Util, SolrSearchService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {

            getConfig: {
                url: "proxy/arkcase/api/latest/service/config/:name"
                , method: "GET"
                , cache: true
            }
            , getConfig_tmp: {
                url: "proxy/arkcase/api/latest/service/config/:name"
                , method: "GET"
                , cache: true
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
                url: "proxy/arkcase/api/latest/plugin/search/advanced/USER/all"
                , method: "GET"
                , cache: true
                , isArray: true
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
                url: "proxy/arkcase/api/latest/plugin/search/USER?n=1000&s=name asc"
                , method: "GET"
                , cache: true
            }

        });


        Service.SessionCacheNames = {
            USERS: "AcmUsers"
            , USERS_BASIC: "AcmUsersBasic"
            , USER_FULL_NAMES: "AcmUserFullNames"
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
            if (!SolrSearchService.validateSolrData(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);