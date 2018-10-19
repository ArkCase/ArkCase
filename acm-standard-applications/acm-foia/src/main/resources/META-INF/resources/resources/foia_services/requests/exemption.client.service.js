'use strict';

/**
 * @ngdoc service
 * @name services:ExemptionService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/requests/exemption.client.service.js services/requests/exemption.client.service.js}

 * ExemptionService contains function to get exemption codes
 */
angular.module('services').factory('ExemptionService', [ '$http', function($http) {

    return {

        getExemptionCodes: function(caseId) {
            return $http({
                url: 'api/latest/service/ecm/file/' + caseId + '/tags',
                method: 'GET',
                isArray: true,
                params: caseId
            });
        },

        saveExemptionStatutes: function(exemptionData) {
            return $http({
                url: 'api/latest/service/ecm/file/exemption/statutes',
                method: 'PUT',
                data: exemptionData
            });
        }

    }



} ]);