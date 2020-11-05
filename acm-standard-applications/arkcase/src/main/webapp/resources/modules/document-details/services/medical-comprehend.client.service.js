'use strict';

angular.module('document-details').factory('DocumentDetails.MedicalComprehendService',
    ['$resource', 'ObjectService', 'UtilService', 'MessageService',
        function ($resource, ObjectService, Util, MessageService) {

            var Service = $resource('api/latest/service', {}, {
                _getComprehendMedicalByMediaId: {
                    method: 'GET',
                    url: 'api/v1/service/comprehendmedical/media/:mediaVersionId',
                    isArray: false
                },
                _createComprehendMedical: {
                    method: 'POST',
                    url: 'api/v1/service/comprehendmedical',
                },
                _createAutomaticComprehendMedical: {
                    method: 'POST',
                    url: 'api/v1/service/comprehendmedical/:mediaVersionId/automatic',
                }
            });

            /**
             * @ngdoc method
             * @name getComprehendMedicalByMediaId
             * @methodOf services:DocumentDetails.MedicalComprehendService
             *
             * @description
             * Get comprehend medical object
             *
             * @param {Number} mediaVersionId  Media Version Id
             *
             * @returns {Object} Promise
             */
            Service.getComprehendMedicalByMediaId = function (mediaVersionId) {
                return Util.serviceCall({
                    service: Service._getComprehendMedicalByMediaId,
                    param: {
                        mediaVersionId: mediaVersionId
                    },
                    onSuccess: function (data) {
                        return data;
                    },
                    onError: function (error) {
                        MessageService.error(error.data);
                        return error;
                    }
                });

            };

            /**
             * @ngdoc method
             * @name createComprehendMedical
             * @methodOf services:DocumentDetails.MedicalComprehendService
             *
             * @description
             * Save comprehend medical object
             *
             * @param {Object} comprehendMedicalObj  Comprehend medical object
             *
             * @returns {Object} Promise
             */
            Service.createComprehendMedical = function (comprehendMedicalObj) {
                return Util.serviceCall({
                    service: Service._createComprehendMedical,
                    data: comprehendMedicalObj,
                    onSuccess: function (data) {
                        return data;
                    },
                    onError: function (error) {
                        MessageService.error(error.data);
                        return error;
                    }
                });
            };


            /**
             * @ngdoc method
             * @name createAutomaticComprehendMedical
             * @methodOf services:DocumentDetails.MedicalComprehendService
             *
             * @description
             * Start automatic comprehend medical
             *
             * @param {Object} mediaVersionId  Active Version of ECM file
             *
             * @returns {Object} Promise
             */
            Service.createAutomaticComprehendMedical = function (mediaVersionId) {
                return Util.serviceCall({
                    service: Service._createAutomaticComprehendMedical,
                    param: {
                        mediaVersionId: mediaVersionId
                    },
                    data: {},
                    onSuccess: function (data) {
                        MessageService.succsessAction();
                        return data;
                    },
                    onError: function (error) {
                        MessageService.error(error.data);
                        return error;
                    }
                });
            };

            return Service;

        }]);