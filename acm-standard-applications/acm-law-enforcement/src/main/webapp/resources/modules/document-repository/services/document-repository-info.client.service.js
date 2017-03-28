'use strict';

/**
 * @ngdoc service
 * @name services:DocumentRepository.InfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/cases/services/case-info.client.service.js modules/cases/services/case-info.client.service.js}
 *
 * Case.InfoService provides functions for Case database data
 */
angular.module('services').factory('DocumentRepository.InfoService', ['$resource', '$translate', 'Acm.StoreService'
    , 'UtilService', 'Object.InfoService', 'Object.ModelService'
    , function ($resource, $translate, Store, Util, ObjectInfoService, ObjectModelService) {
        var Service = $resource('api/latest/plugin', {}, {});

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            DOC_REPO_INFO: "DocumentRepositoryInfo"
        };

        /**
         * @ngdoc method
         * @name saveDocumentRepository
         * @methodOf services:DocumentRepository.InfoService
         *
         * @description
         * Save document repository data
         *
         * @param {Object} repository  DocumentRepository data
         *
         * @returns {Object} Promise
         */
        Service.saveDocumentRepository = function (repository) {
            console.log("Saving repository...");
            console.log(repository);
            if (!Service.validateDocumentRepositoryInfo(repository)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            return Util.serviceCall({
                service: ObjectInfoService.save
                , param: {type: "documentrepository"}
                , data: repository
                , onSuccess: function (data) {
                    console.log("DATA");
                    console.log(data);
                    if (Service.validateDocumentRepositoryInfo(data)) {
                        var cacheDocumentRepositoryInfo = new Store.CacheFifo(Service.CacheNames.DOC_REPO_INFO);
                        cacheDocumentRepositoryInfo.put(data.id, data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateDocumentRepositoryInfo
         * @methodOf services:DocumentRepository.InfoService
         *
         * @description
         * Validate document repository data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateDocumentRepositoryInfo = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.name)) {
                return false;
            }
            if (!Util.isArray(data.participants)) {
                return false;
            }
            if(!ObjectModelService.getParticipantByType(data, "assignee")){
                return false;
            }
            if(!ObjectModelService.getParticipantByType(data, "owning group")){
                return false;
            }
            return true;
        };

        return Service;
    }
]);
