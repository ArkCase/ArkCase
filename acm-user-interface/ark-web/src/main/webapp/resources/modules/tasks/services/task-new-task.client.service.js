/**
 * Created by nick.ferguson on 12/9/2015.
 */
angular.module('tasks').factory('Task.NewTaskService', ['$resource', 'UtilService',
    function ($resource, UtilService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * ngdoc method
             * name get
             * methodOf tasks.service:Task.NewTaskService
             *
             * @description
             * Query task data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Task ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            //get: {
            //    method: 'GET',
            //    url: 'proxy/arkcase/api/latest/plugin/task/byId/:id',
            //    cache: false,
            //    isArray: false
            //}

            /**
             * @ngdoc method
             * @name save
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
                url: 'proxy/arkcase/api/latest/plugin/task/adHocTask',
                cache: false
            }
        });

        Service.saveAdHocTask = function(taskData){
            return Util.serviceCall({
                    service: Service.createNewTask
                    , data: taskData
            })
        };

        return Service;
    }
]);
























