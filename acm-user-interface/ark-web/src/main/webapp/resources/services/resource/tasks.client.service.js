'use strict';

/**
 * @ngdoc service
 * @name services.service:TasksService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/tasks.client.service.js services/resource/tasks.client.service.js}

 * TasksService includes group of REST calls related to Tasks module. Functions are implemented using $resource.
 */
angular.module('services').factory('TasksService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name get
             * @methodOf services.service:TasksService
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
            get: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/task/byId/:id',
                cache: false,
                isArray: false
            }

            /**
             * @ngdoc method
             * @name save
             * @methodOf services.service:TasksService
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
            , save: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/task/save/:id',
                cache: false
            }

            /**
             * @ngdoc method
             * @name queryTasks
             * @methodOf services.service:TasksService
             *
             * @description
             * Query list of tasks from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.start  zero based index of result starts from
             * @param {Number} params.n max number of list to return
             * @param {String} params.sort  sort value. Allowed choice is based on backend specification
             * @param {String} params.filters  filter value. Allowed choice is based on backend specification
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            , queryTasks: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/search/TASK?start=:start&n=:n&sort=:sort&filters=:filters',
                cache: false,
                isArray: false
            }

            /**
             * @ngdoc method
             * @name queryTaskHistory
             * @methodOf services.service:TasksService
             *
             * @description
             * Query list of tasks from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.queryId  Task ID for none ADHOC task; business process ID for ADHOC task
             * @param {Boolean} params.adhoc True if ADHOC task
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            , queryTaskHistory: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/task/history/:queryId/:adhoc',
                cache: false,
                isArray: true
            }


            , queryContacts: {
                url: 'proxy/arkcase/api/latest/plugin/task/byId/:id',
                cache: false,
                isArray: true,
                transformResponse: function (data, headerGetter) {
                    var results = [];
                    var taskObj = JSON.parse(data);
                    if (taskObj && taskObj.personAssociations) {
                        var persons = taskObj.personAssociations;
                        for (var i = 0; i < persons.length; i++) {
                            results.push(persons[i].person);
                        }
                    }
                    return results;
                }
            }
            , addPersonAssociation: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/personAssociation',
                cache: false
            }
            , deletePersonAssociation: {
                method: 'DELETE',
                url: 'proxy/arkcase/api/latest/plugin/personAssociation/delete/:personAssociationId',
                cache: false
            }

            , queryAudit: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/audit/TASK/:id?start=:startWith&n=:count&s=:sort',
                cache: false
            }

            //, queryTasks: {
            //    method: 'GET',
            //    url: 'proxy/arkcase/api/latest/plugin/search/children?parentType=TASK&childType=TASK&parentId=:id&start=:startWith&n=:count&s=:sort',
            //    cache: false
            //}
            , queryMyTasks: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/task/forUser/:user',
                cache: false,
                isArray: true
            }
            , deleteTask: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/task/deleteTask/:taskId',
                cache: false
            }
            , completeTask: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/task/completeTask/:taskId',
                cache: false
            }
            , completeTaskWithOutcome: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/task/completeTask/',
                cache: false
            }

            , queryNotes: {
                method: 'GET',
                //url: 'proxy/arkcase/api/latest/plugin/note/:parentType/:parentId?start=:startWith&n=:count&s=:sort',
                url: 'proxy/arkcase/api/latest/plugin/note/:parentType/:parentId',
                cache: false,
                isArray: true
            }
            , saveNote: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/note/',
                cache: false
            }
            , deleteNote: {
                method: 'DELETE',
                url: 'proxy/arkcase/api/latest/plugin/note/:noteId',
                cache: false
            }

            , queryCorrespondence: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/service/ecm/bycategory/:parentType/:parentId?category=Correspondence&start=:startWith&n=:count&s=:sort',
                cache: false
            }
            , createCorrespondence: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/service/correspondence?templateName=:template&parentObjectType=:parentType&parentObjectId=:parentId&targetCmisFolderId=:folderId',
                cache: false
            }
            , queryTimesheets: {
                method: 'GET',
                //url: 'proxy/arkcase/api/v1/service/timesheet/objectId/:objectId/objectType/:objectType?start=:startWith&n=:count&s=:sort',
                url: 'proxy/arkcase/api/v1/service/timesheet/objectId/:objectId/objectType/:objectType',
                cache: false,
                isArray: true
            }
            , queryCostsheets: {
                method: 'GET',
                //url: 'proxy/arkcase/api/v1/service/costsheet/objectId/:objectId/objectType/:objectType?start=:startWith&n=:count&s=:sort',
                url: 'proxy/arkcase/api/v1/service/costsheet/objectId/:objectId/objectType/:objectType',
                cache: false,
                isArray: true
            }

        });
    }
]);
