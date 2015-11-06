'use strict';

/**
 * @ngdoc service
 * @name services.service:CasesService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/cases.client.service.js services/resource/cases.client.service.js}

 * CasesService includes group of REST calls related to Cases module. Functions are implemented using $resoruce.
 */
angular.module('services').factory('CasesService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {
            get: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/casefile/byId/:id',
                cache: false,
                isArray: false
            }
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
             * @param {Number} params.start  zero based index of result starts from
             * @param {Number} params.n max number of list to return
             * @param {String} params.sort  sort value. Allowed choice is based on backend specification
             * @param {String} params.filters  filter value. Allowed choice is based on backend specification
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
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

            , queryAudit: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/audit/CASE_FILE/:id?start=:startWith&n=:count&s=:sort',
                cache: false
            }

            , queryTasks: {
                method: 'GET',
                url: 'proxy/arkcase/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId=:id&start=:startWith&n=:count&s=:sort',
                cache: false
            }
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
