'use strict';

/**
 * @ngdoc service
 * @name tasks.service:Task.WorkflowService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/tasks/services/task-workflow.client.service.js modules/tasks/services/task-workflow.client.service.js}
 *
 * Task.WorkflowService provides functions for Task workflow
 */
angular.module('tasks').factory('Task.WorkflowService', ['$resource', '$translate', 'UtilService', 'Task.InfoService',
    function ($resource, $translate, Util, TaskInfoService) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _completeTask
             * @methodOf tasks.service:Task.WorkflowService
             *
             * @description
             * Make REST call for completeTask() function to complete a task.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.taskId  Task ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            _completeTask: {
                method: 'POST',
                url: 'api/latest/plugin/task/completeTask/:taskId',
                cache: false
            }

            /**
             * @ngdoc method
             * @name _completeTaskWithOutcome
             * @methodOf tasks.service:Task.WorkflowService
             *
             * @description
             * Make REST call for completeTaskWithOutcome() function to complete a task.
             *
             * @param {Object} data  Task data
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            , _completeTaskWithOutcome: {
                method: 'POST',
                url: 'api/latest/plugin/task/completeTask/',
                cache: false
            }
            /**
             * @ngdoc method
             * @name _deleteTask
             * @methodOf tasks.service:Task.WorkflowService
             *
             * @description
             * Make REST call for deleteTask() function to delete a task.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.taskId  Task ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            , _deleteTask: {
                method: 'POST',
                url: 'api/latest/plugin/task/deleteTask/:taskId',
                cache: false
            }

        });

        Service.WorkflowStatus = {
            COMPLETE: "COMPLETE"
            //other status ?
        };

        /**
         * @ngdoc method
         * @name completeTask
         * @methodOf tasks.service:Task.WorkflowService
         *
         * @description
         * Complete a task
         *
         * @param {Number} taskId  Task ID
         *
         * @returns {Object} Promise
         */
        Service.completeTask = function (taskId) {
            return Util.serviceCall({
                service: Service._completeTask
                , param: {taskId: taskId}
                , data: {}
                , onSuccess: function (data) {
                    if (TaskInfoService.validateTaskInfo(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name completeTaskWithOutcome
         * @methodOf tasks.service:Task.WorkflowService
         *
         * @description
         * Complete a task with outcome.
         *
         * @param {Object} taskInfo  Task data
         *
         * @returns {Object} Promise
         */
        Service.completeTaskWithOutcome = function (taskInfo, outcome) {
            var found = null;
            var fieldsRequiredWhenOutcomeIsChosen = null;
            if (TaskInfoService.validateTaskInfo(taskInfo)) {
                found = _.find(taskInfo.availableOutcomes, {name: outcome});
                fieldsRequiredWhenOutcomeIsChosen = _.result(found, "fieldsRequiredWhenOutcomeIsChosen");
            }
            if (!Util.isArray(fieldsRequiredWhenOutcomeIsChosen)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            for (var i = 0; i < fieldsRequiredWhenOutcomeIsChosen.length; i++) {
                if (Util.isEmpty(taskInfo[fieldsRequiredWhenOutcomeIsChosen[i]])) {
                    return Util.errorPromise($translate.instant("tasks.comp.actions.error.outcomeFieldMissing") + ": " + fieldsRequiredWhenOutcomeIsChosen[i]);
                }
            }
            taskInfo.taskOutcome = found;

            return Util.serviceCall({
                service: Service._completeTaskWithOutcome
                , data: taskInfo
                , onSuccess: function (data) {
                    if (TaskInfoService.validateTaskInfo(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name deleteTask
         * @methodOf tasks.service:Task.WorkflowService
         *
         * @description
         * Delete a task
         *
         * @param {Number} taskId  Task ID
         *
         * @returns {Object} Promise
         */
        Service.deleteTask = function (taskId) {
            return Util.serviceCall({
                service: Service._deleteTask
                , param: {taskId: taskId}
                , data: {}
                , onSuccess: function (data) {
                    if (TaskInfoService.validateTaskInfo(data)) {
                        return data;
                    }
                }
            });
        };


        return Service;
    }
]);
