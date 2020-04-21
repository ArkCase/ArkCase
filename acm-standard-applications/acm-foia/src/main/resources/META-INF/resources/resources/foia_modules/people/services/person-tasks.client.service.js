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
angular.module('services').factory('Person.TaskService', [ '$resource', '$translate', 'UtilService', function($resource, $translate, Util) {
    var Service = $resource('api/latest/plugin', {}, {

        _findTasks: {
            method: 'GET',
            url: 'api/latest/plugin/task/forPerson/:personId',
            cache: false,
            isArray: true
        }

    });

    Service.findTasks = function(personId) {
        return Util.serviceCall({
            service: Service._findTasks,
            param: {
                personId: personId
            },
            data: {},
            onSuccess: function(data) {
                return data;

            },
            onError: function(data) {
                return data;
            }
        });
    };

    return Service;
}
]);
