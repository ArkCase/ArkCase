'use strict';

/**
 * @ngdoc service
 * @name services.service:CallObjectsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/resource/call-objects.client.service.js services/resource/call-objects.client.service.js}

 * CallObjectsService contains wrapper functions of ObjectsService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('CallObjectsService', ['$translate', 'StoreService', 'UtilService', 'ValidationService', 'ObjectsService', 'ConstantService',
    function ($translate, Store, Util, Validator, ObjectsService, Constant) {
        var ServiceCall = {
            SessionCacheNames: {}
            , CacheNames: {
                NOTES: "Notes"
            }

            /**
             * @ngdoc method
             * @name queryNotes
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Query list of notes of an object
             *
             * @param {String} objectType  Object type
             * @param {Number} objectId  Object ID
             *
             * @returns {Object} Promise
             */
            , queryNotes: function (objectType, objectId) {
                var cacheNotes = new Store.CacheFifo(this.CacheNames.NOTES);
                var cacheKey = objectType + "." + objectId;
                var notes = cacheNotes.get(cacheKey);
                return Util.serviceCall({
                    service: ObjectsService.queryNotes
                    , param: {
                        parentType: objectType
                        , parentId: objectId
                    }
                    , result: notes
                    , onSuccess: function (data) {
                        if (ServiceCall.validateNotes(data)) {
                            notes = data;
                            cacheNotes.put(cacheKey, notes);
                            return notes;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name saveNote
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Save note data
             *
             * @param {Object} noteInfo  Note data
             *
             * @returns {Object} Promise
             */
            , saveNote: function (noteInfo) {
                if (0 != noteInfo.id) {     //Don't validate when creating new note; there is no id yet
                    if (!ServiceCall.validateNote(noteInfo)) {
                        return Util.errorPromise($translate.instant("common.service.error.invalidData"));
                    }
                }
                return Util.serviceCall({
                    service: ObjectsService.saveNote
                    , data: noteInfo
                    , onSuccess: function (data) {
                        if (ServiceCall.validateNote(data)) {
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name deleteNote
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Delete a note
             *
             * @param {Number} noteId  Note ID
             *
             * @returns {Object} Promise
             */
            , deleteNote: function (noteId) {
                return Util.serviceCall({
                    service: ObjectsService.deleteNote
                    , param: {noteId: noteId}
                    , data: {}
                    , onSuccess: function (data) {
                        if (ServiceCall.validateDeletedNote(data)) {
                            return data;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateNotes
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Validate notes
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateNotes: function (data) {
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
                return true;
            }

            /**
             * @ngdoc method
             * @name validateNote
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Validate notes
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateNote: function (data) {
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
            }

            /**
             * private method
             * name validateNote
             * methodOf services.service:CallObjectsService
             *
             * @description
             * Validate response of deleted note.
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateDeletedNote: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.deletedNoteId)) {
                    return false;
                }
                return true;
            }

        };

        return ServiceCall;
    }
]);
