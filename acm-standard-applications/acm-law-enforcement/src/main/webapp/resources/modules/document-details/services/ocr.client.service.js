'use strict';

angular.module('document-details').factory('DocumentDetails.OCRService', [ '$resource', 'ObjectService', 'CacheFactory', 'UtilService', 'MessageService', function($resource, ObjectService, CacheFactory, Util, MessageService) {

    var ocrBaseUrl = "api/latest/service/ocr/";
    var Service = $resource('api/latest/service', {}, {
        get: {
            method: 'GET',
            url: 'api/v1/service/ocr/media/:mediaVersionId',
            isArray: false
        },
        getByFileId: {
            method: 'GET',
            url: 'api/v1/service/ocr/file/:fileId',
            isArray: false

        }
    });

    /**
     * @ngdoc method
     * @name getOCR
     * @methodOf services:DocumentDetails.OCRService
     *
     * @description
     * Get OCR object by mediaVersionId
     *
     * @param {Number} mediaVersionId  Media Version Id
     *
     * @returns {Object} Promise
     */
    Service.getOCR = function(mediaVersionId) {
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
     * @name getOCRbyFileId
     * @methodOf services:DocumentDetails.OCRService
     *
     * @description
     * Get OCR object by fileId
     *
     * @param {Number} fileId  File id
     *
     * @returns {Object} Promise
     */
    Service.getOCRbyFileId = function(fileId) {
        return Util.serviceCall({
            service: Service.getByFileId,
            param: {
                fileId: fileId
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


    return Service;

} ]);