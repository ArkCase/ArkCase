'use strict';

/**
 * @ngdoc service
 * @name tasks.service:Task.PeopleService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/tasks/services/task-people.client.service.js modules/tasks/services/task-people.client.service.js}
 *
 * Task.PeopleService provides functions for finding People under Task
 */
angular.module('tasks').factory('Task.PeopleService', [ '$resource', '$translate', 'UtilService', function($resource, $translate, Util) {
    var Service = $resource('api/latest/plugin', {}, {

        _findPeople: {
            method: 'GET',
            url: 'api/latest/plugin/person-associations/:parentType/:parentId',
            cache: false,
        }

    });

    Service.findPeople = function(taskId) {
        return Util.serviceCall({
            service: Service._findPeople,
            param: {
                parentType: 'TASK',
                parentId: taskId
            },
            data: {},
            onSuccess: function(data) {
                return data.response.docs;

            },
            onError: function(data) {
                return data;
            }
        });
    };

    return Service;
}
]);
