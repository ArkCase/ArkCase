'use strict';

/**
 * @ngdoc service
 * @name services.service:ObjectsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/objects.client.service.js services/resource/objects.client.service.js}

 * ObjectsService includes group of REST calls shared by multiple modules, including Cases, Complaints, and Tasks. Functions are implemented using $resource.
 */
angular.module('services').factory('ObjectsService', ['$resource',
    function ($resource) {
        return $resource('proxy/arkcase/api/latest/plugin', {}, {

            /**
             * @ngdoc method
             * @name queryNotes
             * @methodOf services.service:ObjectsService
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
            queryNotes: {
                method: 'GET',
                //url: 'proxy/arkcase/api/latest/plugin/note/:parentType/:parentId?start=:start&n=:count&s=:sort',
                url: 'proxy/arkcase/api/latest/plugin/note/:parentType/:parentId',
                cache: false,
                isArray: true
            }

            /**
             * @ngdoc method
             * @name saveNote
             * @methodOf services.service:ObjectsService
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
             * @methodOf services.service:ObjectsService
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

        });
    }
]);
