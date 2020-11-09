'use strict';

/**
 * @ngdoc service
 * @name services:ExemptionService
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/requests/exemption.client.service.js services/requests/exemption.client.service.js}

 * ExemptionService contains function to get exemption codes
 */
angular.module('services').factory('ExemptionService', [ '$http', function($http) {

    return {

        getDocumentExemptionCodes: function (caseId, fileId) {
            return $http({
                url: 'api/latest/service/ecm/file/' + caseId + '/tags/' + fileId,
                method: 'GET',
                isArray: true,
                params: {
                    caseId: caseId,
                    fileId: fileId
                },
                cache: false
            });
        },

        saveDocumentExemptionCode: function (fileId, exemptionData) {
            return $http({
                url: 'api/latest/service/ecm/file/' + fileId + '/update/tags/manually',
                method: 'POST',
                params: {
                    tags: exemptionData
                }
            });
        },

        getDocumentExemptionStatutes: function (fileId) {
            return $http({
                url: 'api/latest/service/exemption-statute/file/' + fileId,
                method: 'GET',
                isArray: true,
                cache: false
            });
        },

        saveDocumentExemptionStatute: function (fileId, data) {
            return $http({
                url: 'api/latest/service/exemption-statute/file/' + fileId,
                method: 'POST',
                data: data,
                headers: {
                    'Content-Type': 'application/json'
                }
            });
        }
    }

} ]);
