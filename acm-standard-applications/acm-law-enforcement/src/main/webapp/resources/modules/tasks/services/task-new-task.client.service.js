/**
 * @ngdoc service
 * @name tasks.service:Tasks.NewTaskService
 *
 * @description
 *
 * {@Link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/tasks/services/task-new-task.client.service.js modules/tasks/services/task-new-task.client.service.js}
 *
 * Task.NewTaskService provides the functions for creating an Ad Hoc task.
 */
angular.module('tasks').factory('Task.NewTaskService', [ '$http', '$httpParamSerializer', function($http, $httpParamSerializer) {
    var Service = this;

    /**
     * @ngdoc method
     * @name saveAdHocTask
     * @methodOf tasks.service:Task.NewTaskService
     *
     * @description
     * Save ad hoc task data
     *
     * @param {Object} taskData Data from the ad hod task to be created
     *
     * @returns {Promise}
     */
    Service.saveAdHocTask = function(taskData) {
        return $http({
            method: 'POST',
            url: 'api/latest/plugin/task/adHocTask',
            data: taskData,
            cache: false,
            headers: {
                'Content-Type': undefined
            }
        });
    };

    /**
     * @ngdoc method
     * @name createWorkflowTask
     * @methodOf tasks.service:Task.NewTaskService
     *
     * @description
     * Create task to review selected documents
     *
     * @param {Object} taskData Data from the ad hod task to be created
     * @param {String} businessProcessName The name of the business process that we want to start
     *
     * @returns {Promise}
     */
    Service.reviewDocuments = function(taskData, businessProcessName) {
        var params = {
            businessProcessName: businessProcessName
        };

        var urlArgs = $httpParamSerializer(params);

        return $http({
            method: 'POST',
            url: 'api/latest/plugin/tasks/documents/review' + '?' + urlArgs,
            data: taskData,
            cache: false
        });
    };
    
    Service.reviewNewDocuments = function(formData, businessProcessName) {
        var params = {
            businessProcessName: businessProcessName
        };

        var urlArgs = $httpParamSerializer(params);

        return $http({
            method: 'POST',
            url: 'api/latest/plugin/tasks/newdocuments/review'  + '?' + urlArgs,
            data: formData,
            headers: {
                'Content-Type': undefined
            }
        });
    };

    return Service;
} ]);