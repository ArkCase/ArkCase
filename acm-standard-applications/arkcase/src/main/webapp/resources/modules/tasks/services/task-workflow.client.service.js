'use strict';

/**
 * @ngdoc service
 * @name tasks.service:Task.WorkflowService
 *
 * @description
 *
 * {@link /acm-standard-applications/arkcase/src/main/webapp/resources/modules/tasks/services/task-workflow.client.service.js modules/tasks/services/task-workflow.client.service.js}
 *
 * Task.WorkflowService provides functions for Task workflow
 */
angular.module('tasks').factory('Task.WorkflowService', [ '$resource', '$translate', 'UtilService', 'Task.InfoService', 'Acm.StoreService', function($resource, $translate, Util, TaskInfoService, Store) {
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
        ,
        _completeTaskWithOutcome: {
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
        ,
        _deleteTask: {
            method: 'POST',
            url: 'api/latest/plugin/task/deleteTask/:taskId',
            cache: false
        }
        /**
         * @ngdoc method
         * @name _claimTask
         * @methodOf tasks.service:Task.WorkflowService
         *
         * @description
         * Make REST call for claimTask() function to claim a task.
         * It will make the caller the assignee of the task
         * @param {String} taskId  Task ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        ,
        _claimTask: {
            method: 'POST',
            url: 'api/latest/plugin/task/claim/:taskId',
            cache: false
        }

        /**
         * @ngdoc method
         * @name _unclaimTask
         * @methodOf tasks.service:Task.WorkflowService
         *
         * @description
         * Make REST call for unclaimTask() function to unclaim a task.
         * It will make the assignee of the task null and anyone with
         * right access can claim it.
         * @param {String} taskId  Task ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        ,
        _unclaimTask: {
            method: 'POST',
            url: 'api/latest/plugin/task/unclaim/:taskId',
            cache: false
        }

        /**
         * @ngdoc method
         * @name _diagram
         * @methodOf tasks.service:Task.WorkflowService
         *
         * @description
         * Make REST call for diagram() function to get diagram for a task.
         * @param {String} taskId  Task ID
         * @param {Function} onSuccess (Optional)Callback function of success taking the diagram.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {String} Base64 of diagram
         */
        ,
        _diagram: {
            method: 'GET',
            url: 'api/latest/plugin/task/diagram/:taskId',
            cache: false
        }

        /**
         * @ngdoc method
         * @name _diagramByProcessId
         * @methodOf tasks.service:Task.WorkflowService
         *
         * @description
         * Make REST call for diagramByProcessId() function to get diagram for a Process.
         * @param {String} processId  Process ID
         * @param {Function} onSuccess (Optional)Callback function of success taking the diagram.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {String} Base64 of diagram
         */
        ,
        _diagramByProcessId: {
            method: 'GET',
            url: 'api/latest/plugin/task/diagram/process/:processId',
            cache: false
        }

    });

    Service.WorkflowStatus = {
        COMPLETE: "COMPLETE"
    //other status ?
    };

    Service.CacheNames = {
        TASK_DIAGRAM: "TaskDiagram",
        PROCESS_DIAGRAM: "ProcessDiagram"
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
    Service.completeTask = function(taskId) {
        return Util.serviceCall({
            service: Service._completeTask,
            param: {
                taskId: taskId
            },
            data: {},
            onSuccess: function(data) {
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
    Service.completeTaskWithOutcome = function(taskInfo, outcome) {
        var found = null;
        var fieldsRequiredWhenOutcomeIsChosen = null;
        const map = new Map();
        map.set('reworkInstructions', 'Rework Details. Please add Rework Details');
       
        if (TaskInfoService.validateTaskInfo(taskInfo)) {
            found = _.find(taskInfo.availableOutcomes, {
                name: outcome
            });
            fieldsRequiredWhenOutcomeIsChosen = _.result(found, "fieldsRequiredWhenOutcomeIsChosen");
        }
        if (!Util.isArray(fieldsRequiredWhenOutcomeIsChosen)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        for (var i = 0; i < fieldsRequiredWhenOutcomeIsChosen.length; i++) {
            if (Util.isEmpty(taskInfo[fieldsRequiredWhenOutcomeIsChosen[i]])) {
                return Util.errorPromise($translate.instant("tasks.comp.actions.error.outcomeFieldMissing") + " " + map.get(fieldsRequiredWhenOutcomeIsChosen[i]));
            }
        }
        taskInfo.taskOutcome = found;

        return Util.serviceCall({
            service: Service._completeTaskWithOutcome,
            data: taskInfo,
            onSuccess: function(data) {
                if (TaskInfoService.validateTaskInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name claimTask
     * @methodOf tasks.service:Task.WorkflowService
     *
     * @description
     * Claim a task
     *
     * @param {Number} taskId  Task ID
     * @returns {Object} Promise
     */
    Service.claimTask = function(taskId) {
        return Util.serviceCall({
            service: Service._claimTask,
            param: {
                taskId: taskId
            },
            data: {},
            onSuccess: function(data) {
                if (TaskInfoService.validateTaskInfo(data)) {
                    return data;
                }
            },
            onError: function(data) {
                return data;
            }
        });
    };

    /**
     * @ngdoc method
     * @name unclaimTask
     * @methodOf tasks.service:Task.WorkflowService
     *
     * @description
     * Unclaim a task
     *
     * @param {Number} taskId  Task ID
     * @returns {Object} Promise
     */
    Service.unclaimTask = function(taskId) {
        return Util.serviceCall({
            service: Service._unclaimTask,
            param: {
                taskId: taskId
            },
            data: {},
            onSuccess: function(data) {
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
    Service.deleteTask = function(taskId) {
        return Util.serviceCall({
            service: Service._deleteTask,
            param: {
                taskId: taskId
            },
            data: {},
            onSuccess: function(data) {
                if (TaskInfoService.validateTaskInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name diagram
     * @methodOf tasks.service:Task.WorkflowService
     *
     * @description
     * Get diagram for the task
     *
     * @param {Number} taskId  Task ID
     *
     * @returns {Object} Promise
     */
    Service.diagram = function(taskId) {
        var cacheTaskDiagram = new Store.CacheFifo(Service.CacheNames.TASK_DIAGRAM);
        var taskDiagram = cacheTaskDiagram.get(taskId);
        return Util.serviceCall({
            service: Service._diagram,
            param: {
                taskId: taskId
            },
            data: taskDiagram,
            onSuccess: function(data) {
                if (Service.validateDiagramData(data)) {
                    cacheTaskDiagram.put(taskId, data);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name diagram
     * @methodOf tasks.service:Task.WorkflowService
     *
     * @description
     * Get diagram for the process
     *
     * @param {Number} processId  Process ID
     *
     * @returns {Object} Promise
     */
    Service.diagramByProcessId = function(processId) {
        var cacheProcessDiagram = new Store.CacheFifo(Service.CacheNames.PROCESS_DIAGRAM);
        var processDiagram = cacheProcessDiagram.get(processId);
        return Util.serviceCall({
            service: Service._diagramByProcessId,
            param: {
                processId: processId
            },
            data: processDiagram,
            onSuccess: function(data) {
                if (Service.validateDiagramData(data)) {
                    cacheProcessDiagram.put(processId, data);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateDiagramData
     * @methodOf tasks.service:Task.WorkflowService
     *
     * @description
     * Validate diagram data
     *
     * @param {Object} response  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateDiagramData = function(response) {
        if (Util.isEmpty(response)) {
            return false;
        }
        if (Util.isEmpty(response.data)) {
            return false;
        }
        return true;
    };

    return Service;
}

]);
