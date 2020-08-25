'use strict';

angular.module('services').factory('Requests.RequestsService', [ '$resource', function($resource) {
    return $resource('api/latest/plugin', {}, {
        get: {
            method: 'GET',
            cache: false,
            url: 'api/v1/plugin/casefile/byId/:id',
            isArray: false
        },

        queryHistory: {
            method: 'GET',
            cache: false,
            url: 'api/v1/plugin/audit/CASE_FILE/:requestId'
        },

        queryDocument: {
            method: 'GET',
            cache: false,
            url: 'api/v1/service/ecm/folder/CASE_FILE/:requestId'
        },

        retrieveFolderList: {
            method: 'GET',
            url: 'api/latest/service/ecm/folder/:objType/:objId/:folderId?start=:start&n=:n&s=:sortBy&dir=:sortDir',
            cache: false,
            isArray: false
        },

        deleteDocument: {
            method: 'DELETE',
            url: 'api/v1/service/ecm/id/:documentId'
        },

        assignUser: {
            method: 'PUT',
            url: 'api/v1/plugin/request/:requestId/assign/:userId'
        },

        assignCurrentUser: {
            method: 'PUT',
            url: 'api/v1/plugin/request/:requestId/assign'
        },

        getRequestByNumber: {
            method: 'GET',
            cache: false,
            url: 'api/v1/plugin/casefile/bynumber?caseNumber=:caseNumber',
            isArray: false
        }
    });
} ]);