'use strict';

angular.module('services').factory('CasesService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {
            get: {
                method: 'GET',
                cache: false,
                url: 'proxy/arkcase/api/latest/plugin/casefile/byId/:id',
                isArray: false
            },

            queryAudit: {
                method: 'GET',
                cache: false,
                url: 'proxy/arkcase/api/latest/plugin/audit/CASE_FILE/:id?start=:startWith&n=:count&s=:sort'
            },

            queryTasks: {
                method: 'GET',
                cache: false,
                url: 'proxy/arkcase/api/latest/plugin/search/children?parentType=CASE_FILE&childType=TASK&parentId=:id&start=:startWith&n=:count'
            },

            queryContacts: {
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
            },

            queryCases: {
                method: 'GET',
                cache: false,
                url: 'proxy/arkcase/api/latest/plugin/search/CASE_FILE?start=0&n=50',
                isArray: false
            }

            ,save: {
                method: 'POST',
                cache: false,
                url: 'proxy/arkcase/api/latest/plugin/casefile'
            }
        });
    }
]);