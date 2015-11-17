'use strict';

/**
 * @ngdoc service
 * @name services.service:CasesService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/cases.client.service.js services/resource/cases.client.service.js}

 * CasesService includes group of REST calls related to Cases module. Functions are implemented using $resource.
 */
angular.module('services').factory('CasesService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name get
             * @methodOf services.service:CasesService
             *
             * @description
             * Query case data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Case ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            get: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/casefile/byId/:id',
                cache: false,
                isArray: false
            }

            /**
             * @ngdoc method
             * @name queryCases
             * @methodOf services.service:CasesService
             *
             * @description
             * Save case data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Case ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            , save: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/casefile',
                cache: false
            }

            /**
             * @ngdoc method
             * @name queryCases
             * @methodOf services.service:CasesService
             *
             * @description
             * Query list of cases from SOLR.
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.start  Zero based index of result starts from
             * @param {Number} params.n max Number of list to return
             * @param {String} params.sort  Sort value. Allowed choice is based on backend specification
             * @param {String} params.filters  Filter value. Allowed choice is based on backend specification
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , queryCases: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/search/CASE_FILE?start=:start&n=:n&sort=:sort&filters=:filters',
                cache: false,
                isArray: false
            }

            , queryContacts: {
                url: 'proxy/arkcase/api/latest/plugin/casefile/byId/:id',
                cache: false,
                isArray: true,
                transformResponse: function (data, headerGetter) {
                    var results = [];
                    var caseObj = JSON.parse(data);
                    if (caseObj && caseObj.personAssociations) {
                        var persons = caseObj.personAssociations;
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

            //, queryTasks: {
            //    method: 'GET',
            //    url: 'proxy/arkcase/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId=:id&start=:startWith&n=:count&s=:sort',
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

            /**
             * @ngdoc method
             * @name queryNotes
             * @methodOf services.service:CasesService
             *
             * @description
             * Query list of notes for an object.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.parentType  Object type
             * @param {String} params.parentId  Object ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            //* @param {Number} params.start Zero based start number of record
            //* @param {Number} params.count Max Number of list to return
            //* @param {String} params.sort  Sort value, with format 'sortBy sortDir', sortDir can be 'asc' or 'desc'
            , queryNotes: {
                method: 'GET',
                //url: 'proxy/arkcase/api/latest/plugin/note/:parentType/:parentId?start=:start&n=:count&s=:sort',
                url: 'proxy/arkcase/api/latest/plugin/note/:parentType/:parentId',
                cache: false,
                isArray: true
            }

            /**
             * @ngdoc method
             * @name saveNote
             * @methodOf services.service:CasesService
             *
             * @description
             * Create a new note or update an existing note
             *
             * @param {Object} data Task data
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , saveNote: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/note/',
                cache: false
            }

            /**
             * @ngdoc method
             * @name deleteNote
             * @methodOf services.service:CasesService
             *
             * @description
             * Create a new note or update an existing note
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.id  Note ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
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
