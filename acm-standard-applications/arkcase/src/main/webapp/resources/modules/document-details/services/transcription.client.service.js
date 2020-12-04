'use strict';

angular.module('document-details').factory('DocumentDetails.TranscriptionAppService', [ '$resource', 'ObjectService', 'CacheFactory', 'UtilService', 'MessageService', function($resource, ObjectService, CacheFactory, Util, MessageService) {

    var transcriptionCache = CacheFactory(ObjectService.ObjectTypes.TRANSCRIBE, {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });

    var transcriptionBaseUrl = "api/latest/service/transcribe/";
    var Service = $resource('api/latest/service', {}, {
        get: {
            method: 'GET',
            url: 'api/v1/service/transcribe/media/:mediaVersionId',
            cache: transcriptionCache,
            isArray: false
        },
        save: {
            method: 'POST',
            url: 'api/v1/service/transcribe',
            cache: true

        },
        update: {
            method: 'PUT',
            url: 'api/v1/service/transcribe',
            cache: true
        },
        startAutomatic: {
            method: 'POST',
            url: 'api/v1/service/transcribe/:mediaVersionId/automatic',
            cache: true
        },
        complete: {
            method: 'PUT',
            url: 'api/v1/service/transcribe/:id/complete',
            cache: true
        },
        cancel: {
            method: 'PUT',
            url: 'api/v1/service/transcribe/:id/cancel',
            cache: true
        },
        compile: {
            method: 'PUT',
            url: 'api/v1/service/transcribe/:id/compile',
            cache: true
        }

    });

    /**
     * @ngdoc method
     * @name getTranscription
     * @methodOf services:DocumentDetails.TranscriptionAppService
     *
     * @description
     * Get transcribe object
     *
     * @param {Number} mediaVersionId  Media Version Id
     *
     * @returns {Object} Promise
     */

    Service.getTranscription = function(mediaVersionId) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                mediaVersionId: mediaVersionId
            },
            onSuccess: function(data) {
                return data;
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });

    };

    /**
     * @ngdoc method
     * @name createTranscription
     * @methodOf services:DocumentDetails.TranscriptionAppService
     *
     * @description
     * Save transcribe object
     *
     * @param {Object} transcribeObj  Transcribe object
     *
     * @returns {Object} Promise
     */

    Service.createTranscription = function(transcribeObj) {
        return Util.serviceCall({
            service: Service.save,
            data: transcribeObj,
            onSuccess: function(data) {
                if (data.id) {
                    transcriptionCache.put(transcriptionBaseUrl + data.id, data);
                }
                return data;
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    /**
     * @ngdoc method
     * @name updateTranscription
     * @methodOf services:DocumentDetails.TranscriptionAppService
     *
     * @description
     * Update transcribe object
     *
     * @param {Object} transcribeObj  Transcribe object
     *
     * @returns {Object} Promise
     */

    Service.updateTranscription = function(transcribeObj) {
        return Util.serviceCall({
            service: Service.update,
            data: transcribeObj,
            onSuccess: function(data) {
                if (data.id) {
                    transcriptionCache.put(transcriptionBaseUrl + data.id, data);
                }
                return data;
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    /**
     * @ngdoc method
     * @name startAutomaticTranscription
     * @methodOf services:DocumentDetails.TranscriptionAppService
     *
     * @description
     * Start automatic transcription
     *
     * @param {Object} mediaVersionId  Active Version of ECM file
     *
     * @returns {Object} Promise
     */

    Service.startAutomaticTranscription = function(mediaVersionId) {
        return Util.serviceCall({
            service: Service.startAutomatic,
            param: {
                mediaVersionId: mediaVersionId
            },
            data: {},
            onSuccess: function(data) {
                if (data.id) {
                    transcriptionCache.put(transcriptionBaseUrl + data.id, data);
                }
                return data;
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    /**
     * @ngdoc method
     * @name completeManualTranscription
     * @methodOf services:DocumentDetails.TranscriptionAppService
     *
     * @description
     * Complete manual transcription
     *
     * @param {Object} transcribeObjId  Transcribe object id
     *
     * @returns {Object} Promise
     */

    Service.completeManualTranscription = function(transcribeObjId) {
        return Util.serviceCall({
            service: Service.complete,
            param: {
                id: transcribeObjId
            },
            data: {},
            onSuccess: function(data) {
                if (data.id) {
                    transcriptionCache.put(transcriptionBaseUrl + data.id, data);
                }
                return data;
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    /**
     * @ngdoc method
     * @name completeManualTranscription
     * @methodOf services:DocumentDetails.TranscriptionAppService
     *
     * @description
     * Complete manual transcription
     *
     * @param {Object} transcribeObjId  Transcribe object id
     *
     * @returns {Object} Promise
     */

    Service.cancelManualTranscription = function(transcribeObjId) {
        return Util.serviceCall({
            service: Service.cancel,
            param: {
                id: transcribeObjId
            },
            data: {},
            onSuccess: function(data) {
                if (data.id) {
                    transcriptionCache.put(transcriptionBaseUrl + data.id, data);
                }
                return data;
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    /**
     * @ngdoc method
     * @name compileTranscription
     * @methodOf services:DocumentDetails.TranscriptionAppService
     *
     * @description
     * Compile word document with transcription
     *
     * @param {Object} transcribeObjId  Transcribe object id
     *
     * @returns {Object} Promise
     */

    Service.compileTranscription = function(transcribeObjId) {
        return Util.serviceCall({
            service: Service.compile,
            param: {
                id: transcribeObjId
            },
            data: {},
            onSuccess: function(data) {
                if (data.id) {
                    transcriptionCache.put(transcriptionBaseUrl + data.id, data);
                }
                return data;
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    return Service;

} ]);