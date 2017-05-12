'use strict';

/**
 * @ngdoc service
 * @name services:DocumentRepository.InfoService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/document-repository/services/document-repository-info.client.service.js modules/document-repository/services/document-repository-info.client.service.js}
 *
 * DocumentRepository.InfoService provides functions for Document Repository database data
 */
angular.module('services').factory('DocumentRepository.InfoService', ['$resource', '$translate', 'Acm.StoreService'
    , 'UtilService', 'Object.InfoService', 'Object.ModelService'
    , function ($resource, $translate, Store, Util, ObjectInfoService, ObjectModelService) {
        var Service = $resource('api/latest/plugin/documentrepository', {}, {

            _getDocumentRepository: {
                method: 'GET',
                url: 'api/latest/plugin/documentrepository/:id',
                cache: false
            }
            , _deleteDocumentRepository: {
                method: 'DELETE',
                url: 'api/latest/plugin/documentrepository/:id'
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            DOC_REPO_INFO: "DocumentRepositoryInfo"
        };

        /**
         * @ngdoc method
         * @name resetDocumentRepositoryInfo
         * @methodOf services:DocumentRepository.InfoService
         *
         * @description
         * Reset document repository info
         *
         * @returns None
         */
        Service.resetDocumentRepositoryInfo = function () {
            var cacheDocumentRepositoryInfo = new Store.CacheFifo(Service.CacheNames.DOC_REPO_INFO);
            cacheDocumentRepositoryInfo.reset();
        };

        /**
         * @ngdoc method
         * @name updateDocumentRepositoryInfo
         * @methodOf services:DocumentRepository.InfoService
         *
         * @description
         * Update document repository data in local cache. No REST call to backend.
         *
         * @param {Object} documentRepositoryInfo  Document Repository data
         *
         * @returns {Object} Promise
         */
        Service.updateDocumentRepositoryInfo = function (documentRepositoryInfo) {
            if (Service.validateDocumentRepositoryInfo(documentRepositoryInfo)) {
                var cacheDocumentRepositoryInfo = new Store.CacheFifo(Service.CacheNames.DOC_REPO_INFO);
                cacheDocumentRepositoryInfo.put(documentRepositoryInfo.id, documentRepositoryInfo);
            }
        };

        /**
         * @ngdoc method
         * @name getDocumentRepositoryInfo
         * @methodOf services:DocumentRepository.InfoService
         *
         * @description
         * Query document repository data
         *
         * @param {Number} id  Document Repository ID
         *
         * @returns {Object} Promise
         */
        Service.getDocumentRepositoryInfo = function (id) {
            var cacheDocumentRepositoryInfo = new Store.CacheFifo(Service.CacheNames.DOC_REPO_INFO);
            var docRepoInfo = cacheDocumentRepositoryInfo.get(id);
            return Util.serviceCall({
                service: Service._getDocumentRepository
                , param: {
                    id: id
                }
                , result: docRepoInfo
                , onSuccess: function (data) {
                    if (Service.validateDocumentRepositoryInfo(data)) {
                        cacheDocumentRepositoryInfo.put(id, data);
                        return data;
                    }
                }
            });
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
            if (!Service.validateDocumentRepositoryInfo(repository)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            return Util.serviceCall({
                service: ObjectInfoService.save
                , param: {
                    type: "documentrepository"
                }
                , data: repository
                , onSuccess: function (data) {
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
         * @name deleteDocumentRepository
         * @methodOf services:DocumentRepository.InfoService
         *
         * @description
         * Delete document repository data
         *
         * @param {Object} repository  DocumentRepository data to be deleted
         *
         * @returns {Object} Promise
         */
        Service.deleteDocumentRepository = function (id) {
            return Util.serviceCall({
                service: Service._deleteDocumentRepository
                , param: {
                    id: id
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
            if (!ObjectModelService.getParticipantByType(data, "assignee")) {
                return false;
            }
            if (!ObjectModelService.getParticipantByType(data, "owning group")) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
