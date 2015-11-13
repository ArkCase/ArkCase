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
                , AUDIT_DATA: "AuditData"
                , SIGNATURES: "Signatures"
            }


            /**
             * @ngdoc method
             * @name queryAudit
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Query audit history for an object.
             *
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Number} params.start Zero based start number of record
             * @param {Number} params.n Max Number of list to return
             * @param {String} params.sort  Sort value, with format 'sortBy sortDir', sortDir can be 'asc' or 'desc'
             *
             * @returns {Object} Promise
             */
            , queryAudit: function (objectType, objectId, start, n, sortBy, sortDir) {
                var cacheCaseAuditData = new Store.CacheFifo(this.CacheNames.AUDIT_DATA);
                var cacheKey = objectType + "." + objectId + "." + start + "." + n + "." + sort;
                var auditData = cacheCaseAuditData.get(cacheKey);

                var sort = "";
                if (!Util.isEmpty(sortBy)) {
                    sort = sortBy + " " + Util.goodValue(sortDir, "asc");
                }
                //implement filtering here when service side supports it
                //var filter = "";
                //filters = [{by: "eventDate", with: "term"}];

                return Util.serviceCall({
                    service: ObjectsService.queryAudit
                    , param: {
                        objectType: objectType
                        , objectId: objectId
                        , start: start
                        , n: n
                        , sort: sort
                    }
                    , onSuccess: function (data) {
                        if (ServiceCall.validateAuditData(data)) {
                            auditData = data;
                            cacheCaseAuditData.put(cacheKey, auditData);
                            return auditData;

                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateAuditData
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Validate audit data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateAuditData: function (data) {
                if (!Util.isArray(data.resultPage)) {
                    return false;
                }
                for (var i = 0; i < data.resultPage.length; i++) {
                    if (!this.validateEvent(data.resultPage[i])) {
                        return false;
                    }
                }
                if (Util.isEmpty(data.totalCount)) {
                    return false;
                }
                return true;
            }

            /**
             * @ngdoc method
             * @name validateEvent
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Validate event data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateEvent: function (data) {
                if (Util.isEmpty(data)) {
                    return false;
                }
                if (Util.isEmpty(data.eventDate)) {
                    return false;
                }
                //if (Util.isEmpty(data.eventType)) {
                //    return false;
                //}
                if (Util.isEmpty(data.objectId)) {
                    return false;
                }
                if (Util.isEmpty(data.objectType)) {
                    return false;
                }
                if (Util.isEmpty(data.userId)) {
                    return false;
                }
                return true;
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

            /**
             * @ngdoc method
             * @name queryRejectComments
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
            , queryRejectComments: function (objectType, objectId) {
                var noteType = "REJECT_COMMENT";
                var cacheNotes = new Store.CacheFifo(this.CacheNames.NOTES);
                var cacheKey = objectType + "." + objectId + "." + noteType;
                var notes = cacheNotes.get(cacheKey);
                return Util.serviceCall({
                    service: ObjectsService.queryNotesByType
                    , param: {
                        parentType: objectType
                        , parentId: objectId
                        , noteType: noteType
                    }
                    , result: notes
                    , onSuccess: function (data) {
                        if (ServiceCall.validateRejectComments(data)) {
                            notes = data;
                            cacheNotes.put(cacheKey, notes);
                            return notes;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateRejectComments
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Validate reject comments (notes)
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateRejectComments: function (data) {
                if (!Util.isArray(data)) {
                    return false;
                }
                return true;
            }

            /**
             * @ngdoc method
             * @name findSignatures
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
            , findSignatures: function (objectType, objectId) {
                var cacheSignatures = new Store.CacheFifo(this.CacheNames.SIGNATURES);
                var cacheKey = objectType + "." + objectId;
                var signatures = cacheSignatures.get(cacheKey);
                return Util.serviceCall({
                    service: ObjectsService.findSignatures
                    , param: {
                        objectType: objectType
                        , objectId: objectId
                    }
                    , result: signatures
                    , onSuccess: function (data) {
                        if (ServiceCall.validateSignatures(data)) {
                            signatures = data;
                            cacheSignatures.put(cacheKey, signatures);
                            return signatures;
                        }
                    }
                });
            }

            /**
             * @ngdoc method
             * @name validateSignatures
             * @methodOf services.service:CallObjectsService
             *
             * @description
             * Validate list of signature data
             *
             * @param {Object} data  Data to be validated
             *
             * @returns {Boolean} Return true if data is valid
             */
            , validateSignatures: function (data) {
                if (!Util.isArray(data)) {
                    return false;
                }
                return true;
            }


        };

        return ServiceCall;
    }
]);
