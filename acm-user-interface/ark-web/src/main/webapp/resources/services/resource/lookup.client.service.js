'use strict';

angular.module('services').factory('LookupService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {

            getPriorities: {
                url: "proxy/arkcase/api/latest/plugin/complaint/priorities"
                ,method: "GET"
                ,cache: true
                ,isArray: true
            }
            ,getUsers: {
                url: "proxy/arkcase/api/latest/plugin/search/advanced/USER/all"
                ,method: "GET"
                ,cache: true
                ,isArray: true
            }
            ,getUsersBasic: {
                url: "proxy/arkcase/api/latest/plugin/search/USER?n=1000&s=name asc"
                ,method: "GET"
                ,cache: true
            }
            ,getGroups: {
                url: "proxy/arkcase/api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc"
                ,method: "GET"
                ,cache: true
            }
            ,getPersonTypes: {
                url: "proxy/arkcase/api/latest/plugin/person/types"
                ,method: "GET"
                ,cache: true
                ,isArray: true
            }
            ,getParticipantTypes: {
                url: "modules_config/config/modules/cases/resources/participantTypes.json"
                ,method: "GET"
                ,cache: true
            }
            ,getPersonTitles: {
                url: "modules_config/config/modules/cases/resources/personTitles.json"
                ,method: "GET"
                ,cache: true
            }
            ,getContactMethodTypes: {
                url: "modules_config/config/modules/cases/resources/contactMethodTypes.json"
                ,method: "GET"
                ,cache: true
            }
            ,getOrganizationTypes: {
                url: "modules_config/config/modules/cases/resources/organizationTypes.json"
                ,method: "GET"
                ,cache: true
            }
            ,getAddressTypes: {
                url: "modules_config/config/modules/cases/resources/addressTypes.json"
                ,method: "GET"
                ,cache: true
            }
            ,getAliasTypes: {
                url: "modules_config/config/modules/cases/resources/aliasTypes.json"
                ,method: "GET"
                ,cache: true
            }
            ,getSecurityTagTypes: {
                url: "modules_config/config/modules/cases/resources/securityTagTypes.json"
                ,method: "GET"
                ,cache: true
            }

            ,getObjectTypes: {
                url: "modules_config/config/modules/cases/resources/objectTypes.json"
                ,method: "GET"
                ,cache: true
                ,isArray: true
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