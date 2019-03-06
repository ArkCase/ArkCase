'use strict';

/**
 * @ngdoc service
 * @name services:Core.UploadManagerModalService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/core/services/upload-manager-modal.client.service.js modules/core/services/upload-manager-modal.client.service.js}
 *
 * Core.UploadManagerModalService provides functions for Large File Upload manager
 */
angular.module('services').factory('Core.UploadManagerModalService',
        [ '$translate', '$timeout', '$resource', '$http', '$log', 'UtilService', 'CacheFactory', 'ObjectService', 'Upload', 'MessageService', function($translate, $timeout, $resource, $http, $log, Util, CacheFactory, ObjectService, Upload, MessageService) {

            var Service = $resource('api/latest/service/ecm', {}, {

                _mergeChunks: {
                    method: 'POST',
                    url: 'api/latest/service/ecm/mergeChunks',
                    isArray: false
                }

            });

            /**
             * @ngdoc method
             * @name mergeChunks
             * @methodOf services:Core.UploadManagerModalService
             *
             * @description
             * Merges smaller chunks into one big file
             *
             * @returns object
             */
            Service.mergeChunks = function(data) {
                return Util.serviceCall({
                    service: Service._mergeChunks,
                    data: data,
                    onSuccess: function(data) {
                        return data;
                    }
                });
            };

            return Service;
        } ]);