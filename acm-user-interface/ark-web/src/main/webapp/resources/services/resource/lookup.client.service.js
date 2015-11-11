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
angular.module('services').factory('LookupService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {

            getConfig: {
                url: "proxy/arkcase/api/latest/service/config/:name"
                , method: "GET"
                , cache: true
            }
            , getCaseTypes: {
                url: 'proxy/arkcase/api/latest/plugin/casefile/caseTypes'
                , cache: true
                , isArray: true
            }

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
            , getUsers: {
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

            /**
             * @ngdoc method
             * @name get
             * @methodOf services.service:LookupService
             *
             * @description
             * Query list of priorities
             *
             * @returns {Object} An array returned by $resource
             */
            , getPriorities: {
                url: "proxy/arkcase/api/latest/plugin/complaint/priorities"
                , method: "GET"
                , cache: true
                , isArray: true
            }
            , getGroups: {
                url: "proxy/arkcase/api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc"
                , method: "GET"
                , cache: true
            }
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
            , getFileTypes: {
                url: "modules_config/config/modules/cases/resources/fileTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }
            , getPlainforms: {
                url: "proxy/arkcase/api/latest/plugin/admin/plainforms/:objType"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            , getCorrespondenceForms: {
                url: "modules_config/config/modules/cases/resources/correspondenceForms.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }


        });
    }
]);