'use strict';

/**
 * @ngdoc service
 * @name services.service:CallLookupService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-lookup.client.service.js services/resource/call-lookup.client.service.js}

 * CallLookupService contains wrapper functions of LookupService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('CallLookupService', ['$resource', '$translate', 'StoreService', 'UtilService', 'ValidationService', 'LookupService', 'ConstantService',
    function ($resource, $translate, Store, Util, Validator, LookupService, Constant) {
        var ServiceCall = {
            SessionCacheNames: {
                USER_INFO: "AcmUserInfo"
                , USERS: "AcmUsers"
                , PRIORITIES: "AcmPriorities"


                , USER_FULL_NAMES: "AcmUserFullNames"
                , GROUPS: "AcmGroups"
                , OBJECT_TYPES: "AcmObjectTypes"
                , FILE_TYPES: "AcmFileTypes"
                , FORM_TYPES: "AcmFormTypes"
                , PARTICIPANT_TYPES: "AcmParticipantTypes"
                , PARTICIPANT_USERS: "AcmParticipantUsers"
                , PARTICIPANT_GROUPS: "AcmParticipantGroups"
                , PERSON_TYPES: "AcmPersonTypes"
                , CONTACT_METHOD_TYPES: "AcmContactMethodTypes"
                , ORGANIZATION_TYPES: "AcmOrganizationTypes"
                , ADDRESS_TYPES: "AcmAddressTypes"
                , ALIAS_TYPES: "AcmAliasTypes"
                , SECURITY_TAG_TYPES: "AcmSecurityTagTypes"

            }
            , CacheNames: {}




            /**
             * @ngdoc method
             * @name getUsers
             * @methodOf services.service:CallLookupService
             *
             * @description
             * Query list of users
             *
             * @returns {Object} An array returned by $resource
             */
            , getUsers: function () {
                var cacheUsers = new Store.SessionData(this.SessionCacheNames.USERS);
                var users = cacheUsers.get();
                return Util.serviceCall({
                    service: LookupService.getUsers
                    , result: users
                    , onSuccess: function (data) {
                        if (ServiceCall.validateUsersRaw(data)) {
                            users = [];
                            _.each(data, function (item) {
                                users.push(Util.goodJsonObj(item));
                            });
                            cacheUsers.set(users);
                            return users;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateUsersRaw
             * @methodOf services.service:CallLookupService
             *
             * @description
             * Validate user list data in with each entry as stringify'ed JSON.
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateUsersRaw: function (data) {
                if (!Util.isArray(data)) {
                    return false;
                }
                return true;
            }


            /**
             * @ngdoc method
             * @name getPriorities
             * @methodOf services.service:CallLookupService
             *
             * @description
             * Query list of priorities
             *
             * @returns {Object} An array returned by $resource
             */
            , getPriorities: function () {
                var cachePriorities = new Store.SessionData(this.SessionCacheNames.PRIORITIES);
                var priorities = cachePriorities.get();
                return Util.serviceCall({
                    service: LookupService.getPriorities
                    , result: priorities
                    , onSuccess: function (data) {
                        if (ServiceCall.validatePriorities(data)) {
                            cachePriorities.set(data);
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validatePriorities
             * @methodOf services.service:CallLookupService
             *
             * @description
             * Validate list of priorities data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validatePriorities: function (data) {
                if (!Util.isArray(data)) {
                    return false;
                }
                return true;
            }

        };

        return ServiceCall;
    }
]);
