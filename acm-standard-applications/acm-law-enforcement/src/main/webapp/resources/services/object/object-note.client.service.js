'use strict';

/**
 * @ngdoc service
 * @name services:Object.NoteService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-note.client.service.js services/object/object-note.client.service.js}

 * Object.NoteService includes group of REST calls related to note.
 */
angular.module('services').factory('Object.NoteService', ['$resource', 'Acm.StoreService', 'UtilService', '$translate',
    function ($resource, Store, Util, $translate) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _queryNotes
             * @methodOf services:Object.NoteService
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
            _queryNotes: {
                method: 'GET',
                //url: 'api/latest/plugin/note/:parentType/:parentId?start=:start&n=:count&s=:sort',
                url: 'api/latest/plugin/note/:parentType/:parentId',
                cache: false,
                isArray: true
            }

            /**
             * @ngdoc method
             * @name _saveNote
             * @methodOf services:Object.NoteService
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
            , _saveNote: {
                method: 'POST',
                url: 'api/latest/plugin/note/',
                cache: false
            }

            /**
             * @ngdoc method
             * @name _deleteNote
             * @methodOf services:Object.NoteService
             *
             * @description
             * Create a new note or update an existing note
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.noteId  Note ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _deleteNote: {
                method: 'DELETE',
                url: 'api/latest/plugin/note/:noteId',
                cache: false
            }


            /**
             * @ngdoc method
             * @name _queryNotesByType
             * @methodOf services:Object.NoteService
             *
             * @description
             * Query list of notes by note type.
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.parentType  Object type
             * @param {Number} params.parentId  Object ID
             * @param {String} params.noteType  Note type
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _queryNotesByType: {
                method: 'GET',
                url: 'api/latest/plugin/note/:parentType/:parentId?type=:noteType',
                cache: false,
                isArray: true
            }

        });


        Service.SessionCacheNames = {};
        Service.CacheNames = {
            NOTES: "Notes"
        };

        /**
         * @ngdoc method
         * @name queryNotes
         * @methodOf services:Object.NoteService
         *
         * @description
         * Query list of notes of an object
         *
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.queryNotes = function (objectType, objectId, noteType) {
            noteType = noteType || "GENERAL";
            var cacheNotes = new Store.CacheFifo(Service.CacheNames.NOTES);
            var cacheKey = objectType + "." + objectId + "." + noteType;
            var notes = cacheNotes.get(cacheKey);
            return Util.serviceCall({
                service: Service._queryNotesByType
                , param: {
                    parentType: objectType
                    , parentId: objectId
                    , noteType: noteType
                }
                , result: notes
                , onSuccess: function (data) {
                    if (Service.validateNotes(data)) {
                        notes = data;
                        cacheNotes.put(cacheKey, notes);
                        return notes;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name saveNote
         * @methodOf services:Object.NoteService
         *
         * @description
         * Save note data
         *
         * @param {Object} noteInfo  Note data
         *
         * @returns {Object} Promise
         */
        Service.saveNote = function (noteInfo) {
            if (noteInfo.id && 0 != noteInfo.id) {     //Don't validate when creating new note; there is no id yet
                if (!Service.validateNote(noteInfo)) {
                    return Util.errorPromise($translate.instant("common.service.error.invalidData"));
                }
            }
            return Util.serviceCall({
                service: Service._saveNote
                , data: noteInfo
                , onSuccess: function (data) {
                    if (Service.validateNote(data)) {
                        var noteInfo = data;
                        var cacheKey = Util.goodValue(noteInfo.parentType) + "." + Util.goodValue(noteInfo.parentId, 0) + "." + Util.goodValue(noteInfo.type, "GENERAL");
                        var cacheNotes = new Store.CacheFifo(Service.CacheNames.NOTES);
                        var notes = cacheNotes.get(cacheKey);
                        if (notes == null)
                            notes = [];
                        //update noteInfo into notes
                        var index = _.findIndex(notes, function (note) {
                            return Util.compare(note.id, noteInfo.id);
                        });
                        if (index < 0)
                            notes.push(noteInfo);
                        else
                            notes[index] = noteInfo;
                        cacheNotes.put(cacheKey, notes);
                        return notes;
                    }
                }
            });
        };


        /**
         * @ngdoc method
         * @name deleteNote
         * @methodOf services:Object.NoteService
         *
         * @description
         * Delete a note
         *
         * @param {Number} noteId  Note ID
         *
         * @returns {Object} Promise
         */
        Service.deleteNote = function (noteId) {
            return Util.serviceCall({
                service: Service._deleteNote
                , param: {noteId: noteId}
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateDeletedNote(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateNotes
         * @methodOf services:Object.NoteService
         *
         * @description
         * Validate notes
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateNotes = function (data) {
            /*
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateNote(data[i])) {
                    return false;
                }
            }
            */
            return true;
        };

        /**
         * @ngdoc method
         * @name validateNote
         * @methodOf services:Object.NoteService
         *
         * @description
         * Validate notes
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateNote = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.parentId)) {
                return false;
            }
            return true;
        };

        /**
         * private method
         * name validateDeletedNote
         * methodOf services:Object.NoteService
         *
         * @description
         * Validate response of deleted note.
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateDeletedNote = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
