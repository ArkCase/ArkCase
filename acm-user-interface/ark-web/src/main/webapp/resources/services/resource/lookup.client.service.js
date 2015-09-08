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
                url: "proxy/arkcase/api/latest/plugin/search/USER?n=1000&s=name asc"
                ,method: "GET"
                ,cache: true
            }
            ,getGroups: {
                url: "proxy/arkcase/api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc"
                ,method: "GET"
                ,cache: true
            }
            ,getParticipantTypes: {
                url: "modules_config/config/modules/cases/resources/participantTypes.json"
                ,method: "GET"
                ,cache: true
            }

        });
    }
]);