'use strict';

/**
 * @ngdoc service
 * @name services:Helper.NoteService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/helper/helper-note.client.service.js services/helper/helper-note.client.service.js}

 * Helper.NoteService provides common functionality related to the notes tables throughout the application
 */
angular.module('services').factory('Helper.NoteService', ['UtilService', 'Object.NoteService', 'Util.DateService'
    , function (Util, ObjectNoteService, UtilDateService) {
        var Service = {

            /**
             * @ngdoc method
             * @name Note Constructor
             * @methodOf services:Helper.NoteService
             *
             * @description
             * Helper.NoteService.Note provides common functionality for the notes controllers
             */
            Note: function () {
            }
        };

        Service.Note.prototype = {

            /**
             * @ngdoc method
             * @name createNote
             * @methodOf services:Helper.NoteService
             *
             * @description
             * Creates an object holding the metadata for one row in the notes table
             *
             * @param {Number} parentObjId unique identifier for the parent of the note
             * @param {String} parentObjType specifies the ArkCase type for the parent of the note
             * @param {String} parentObjTitle specifies the ArkCase title for the parent of the note
             * @param {String} parentObjVersion specifies the parent object version when the note was created
             * @param {String} userId the internal username of the note creator
             * @param {String} noteType type of the note
             * @returns {Object} note metadata used to populate a row in the notes table
             */

            createNote: function (parentObjId, parentObjType, parentObjTitle, parentObjVersion, creator, noteType, author) {
                noteType = noteType || "GENERAL";
                return {
                    parentId: parentObjId,
                    parentType: parentObjType,
                    parentTitle: parentObjTitle,
                    tag: parentObjVersion,
                    created: UtilDateService.dateToIso(new Date()),
                    creator: creator,
                    author: Util.isEmpty(author) ? creator : author,
                    type: noteType
                };
            }

            /**
             * @ngdoc method
             * @name saveNote
             * @methodOf services:Helper.NoteService
             *
             * @description
             * Register an handler function when grid API is ready.
             *
             * @param {Object} note object which contains the note metadata
             * @param {Object} rowEntity contains the metadata for one row of the notes table
             */
            , saveNote: function (note, rowEntity) {
                // The date string needs to be reformatted so that it can be accepted by the backend
                // It is expected to be in ISO format
                note.created = Util.dateToIsoString(new Date(note.created));

                // Persists the note in the ArkCase database
                ObjectNoteService.saveNote(note).then(
                    function (noteAdded) {
                        if (rowEntity) {
                            if (Util.isEmpty(rowEntity.id)) {
                                rowEntity.id = noteAdded.id;
                            }
                        }
                    }
                );
            }

        };
        return Service;
    }
]);