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
angular.module('services').factory('LookupService', ['$resource', 'StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {

            getConfig: {
                url: "proxy/arkcase/api/latest/service/config/:name"
                , method: "GET"
                , cache: true
            }
            //, getCaseTypes: {
            //    url: 'proxy/arkcase/api/latest/plugin/casefile/caseTypes'
            //    , cache: true
            //    , isArray: true
            //}

            /**
             * @ngdoc method
             * @name _getUsers
             * @methodOf services.service:LookupService
             *
             * @description
             * Query list of users
             *
             * @returns {Object} An array returned by $resource
             */
            , _getUsers: {
                url: "proxy/arkcase/api/latest/plugin/search/advanced/USER/all"
                , method: "GET"
                , cache: true
                , isArray: true
            }
            , getUsersBasic: {
                url: "proxy/arkcase/api/latest/plugin/search/USER?n=1000&s=name asc"
                , method: "GET"
                , cache: true
            }

            ///**
            // * @ngdoc method
            // * @name get
            // * @methodOf services.service:LookupService
            // *
            // * @description
            // * Query list of priorities
            // *
            // * @returns {Object} An array returned by $resource
            // */
            //, getPriorities: {
            //    url: "proxy/arkcase/api/latest/plugin/complaint/priorities"
            //    , method: "GET"
            //    , cache: true
            //    , isArray: true
            //}
            //, getGroups: {
            //    url: "proxy/arkcase/api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc"
            //    , method: "GET"
            //    , cache: true
            //}
            , getPersonTypes: {
                url: "proxy/arkcase/api/latest/plugin/person/types"
                , method: "GET"
                , cache: true
                , isArray: true
            }
            , getParticipantTypes: {
                url: "modules_config/config/modules/cases/resources/participantTypes.json"
                , method: "GET"
                , cache: true
            }
            , getPersonTitles: {
                url: "modules_config/config/modules/cases/resources/personTitles.json"
                , method: "GET"
                , cache: true
            }
            , getContactMethodTypes: {
                url: "modules_config/config/modules/cases/resources/contactMethodTypes.json"
                , method: "GET"
                , cache: true
            }
            , getOrganizationTypes: {
                url: "modules_config/config/modules/cases/resources/organizationTypes.json"
                , method: "GET"
                , cache: true
            }
            , getAddressTypes: {
                url: "modules_config/config/modules/cases/resources/addressTypes.json"
                , method: "GET"
                , cache: true
            }
            , getAliasTypes: {
                url: "modules_config/config/modules/cases/resources/aliasTypes.json"
                , method: "GET"
                , cache: true
            }
            , getSecurityTagTypes: {
                url: "modules_config/config/modules/cases/resources/securityTagTypes.json"
                , method: "GET"
                , cache: true
            }

            , getObjectTypes: {
                url: "modules_config/config/modules/cases/resources/objectTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }
            //
            ///**
            // * @ngdoc method
            // * @name getFileTypes
            // * @methodOf services.service:LookupService
            // *
            // * @description
            // * Query list of file types
            // *
            // * @returns {Object} An array returned by $resource
            // */
            //, getFileTypes: {
            //    url: "modules_config/config/modules/cases/resources/fileTypes.json"
            //    , method: "GET"
            //    , cache: true
            //    , isArray: true
            //}
            //
            ///**
            // * @ngdoc method
            // * @name getFormTypes
            // * @methodOf services.service:LookupService
            // *
            // * @description
            // * Query list of form types
            // *
            // * @returns {Object} An array returned by $resource
            // */
            //, getFormTypes: {
            //    url: "proxy/arkcase/api/latest/plugin/admin/plainforms/:objType"
            //    , method: "GET"
            //    , cache: true
            //    , isArray: true
            //}

            , getCorrespondenceForms: {
                url: "modules_config/config/modules/cases/resources/correspondenceForms.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }


        });


        Service.SessionCacheNames = {
            USERS: "AcmUsers"
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
         * @returns {Object} An array returned by $resource
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
         * @returns {Object} An array returned by $resource
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

        return Service;
    }
]);