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
angular.module('tasks').factory('Task.NewTaskService', ['$resource', '$state', 'UtilService', 'Task.InfoService',
    function ($resource, $state, Util, TaskInfoService) {
        var Service = $resource('api/latest/plugin', {}, {

            /**
             * @ngdoc method
             * @name createNewTask
             * @methodOf tasks.service:Task.NewTaskService
             *
             * @description
             * Save task data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Task ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            createNewTask: {
                method: 'POST',
                url: 'api/latest/plugin/task/adHocTask',
                cache: false
            }
        });

        /**
         * @ngdoc method
         * @name saveAdHocTask
         * @methodOf tasks.service:Task.NewTaskService
         *
         * @description
         * Save ad hoc task data
         *
         * @param {Object} taskData Data from the ad hod task to be created
         * @returns {*}
         */
        Service.saveAdHocTask = function (taskData) {
            return Util.serviceCall({
                service: Service.createNewTask
                , data: taskData
                , onSuccess: function (data) {
                    if (TaskInfoService.validateTaskInfo(data)) {
                        $state.go('tasks.main', {type: 'ADHOC', id: data.taskId});
                        return data;
                    }
                }
                , onError: function (errorData) {
                    console.log("ON ERROR");
                    console.log(errorData);
                    return errorData;
                }
            })
        };

        return Service;
    }
]);