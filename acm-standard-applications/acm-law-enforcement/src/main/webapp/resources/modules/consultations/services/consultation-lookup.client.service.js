'use strict';

/**
 * @ngdoc service
 * @name services:Consultation.LookupService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/consultations/services/consultation-lookup.client.service.js modules/consultations/services/consultation-lookup.client.service.js}
 *
 * Case.LookupService provides functions for Consultation lookup data
 */
angular.module('services').factory('Consultation.LookupService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Object.ModelService', 'Object.LookupService', function($resource, $translate, Store, Util, ObjectModelService, ObjectLookupService) {
    var Service = $resource('api/latest/plugin', {}, {
        /**
         * ngdoc method
         * name _getApprovers
         * methodOf services:Consultation.LookupService
         *
         * @description
         * Query list of consultation approvers
         *
         * @returns {Object} Object returned by $resource
         */
        _getApprovers: {
            url: 'api/latest/service/functionalaccess/users/acm-consultation-approve/:group/:assignee',
            cache: true,
            isArray: true
        }
    });

    Service.SessionCacheNames = {
        CONSULTATION_TYPES: "AcmConsultationTypes"
    };
    Service.CacheNames = {
        CONSULTATION_APPROVERS: "ConsultationApprovers"
    };

    /**
     * @ngdoc method
     * @name getConsultationTypes
     * @methodOf services:Consultation.LookupService
     *
     * @description
     * Query list of consultation types
     *
     * @returns {Object} Promise
     */
    Service.getConsultationTypes = function() {
        return ObjectLookupService.getConsultationTypes();
    };

    /**
     * @ngdoc method
     * @name validateConsultationTypes
     * @methodOf services:Consultation.LookupService
     *
     * @description
     * Validate consultation type data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateConsultationTypes = function(data) {
        if (!Util.isArray(data)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name getApprovers
     * @methodOf services:Consultation.LookupService
     *
     * @description
     * Query list of consultation types
     *
     * @param {String} group  Group ID
     * @param {String} assignee  Assignee
     *
     * @returns {Object} Promise
     */
    Service.getApprovers = function(group, assignee) {
        var cacheApprovers = new Store.CacheFifo(Service.CacheNames.CONSULTATION_APPROVERS);
        var cacheKey = group + "." + assignee;
        var approvers = cacheApprovers.get(cacheKey);
        var param = {};
        if (!Util.isEmpty(group)) {
            param.group = group;
        }
        if (!Util.isEmpty(assignee)) {
            param.assignee = assignee;
        }

        return Util.serviceCall({
            service: Service._getApprovers,
            param: param,
            result: approvers,
            onSuccess: function(data) {
                if (Service.validateApprovers(data)) {
                    approvers = data;
                    cacheApprovers.put(cacheKey, approvers);
                    return approvers;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateApprovers
     * @methodOf services:Consultation.LookupService
     *
     * @description
     * Validate consultation approver data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateApprovers = function(data) {
        if (!Util.isArray(data)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);
