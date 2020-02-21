'use strict';

/**
 * @ngdoc service
 * @name services:Complaint.LookupService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/complaints/services/complaint-lookup.client.service.js modules/complaints/services/complaint-lookup.client.service.js}
 *
 * Complaint.LookupService provides functions for Complaint database data
 */
angular.module('services').factory('Complaint.LookupService', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Object.LookupService', function($resource, $translate, Store, Util, ObjectLookupService) {
    var Service = $resource('api/latest/plugin', {}, {
        /**
         * ngdoc method
         * name _getApprovers
         * methodOf services:Complaint.LookupService
         *
         * @description
         * Query list of complaint approvers
         *
         * @returns {Object} Object returned by $resource
         */
        _getApprovers: {
            url: 'api/latest/service/functionalaccess/users/acmComplaintApprovePrivilege/:group/:assignee',
            cache: true,
            isArray: true
        }
    });

    Service.SessionCacheNames = {
        COMPLAINT_TYPES: "AcmComplaintTypes"
    };
    Service.CacheNames = {
        COMPLAINT_APPROVERS: "ComplaintApprovers"
    };

    /**
     * @ngdoc method
     * @name getComplaintTypes
     * @methodOf services:Complaint.LookupService
     *
     * @description
     * Query list of complaint types
     *
     * @returns {Object} Promise
     */
    Service.getComplaintTypes = function() {
        return ObjectLookupService.getComplaintTypes();
    };

    /**
     * @ngdoc method
     * @name validateComplaintTypes
     * @methodOf services:Complaint.LookupService
     *
     * @description
     * Validate complaint type data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateComplaintTypes = function(data) {
        if (!Util.isArray(data)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name getApprovers
     * @methodOf services:Complaint.LookupService
     *
     * @description
     * Query list of complaint types
     *
     * @param {String} group  Group ID
     * @param {String} assignee  Assignee
     *
     * @returns {Object} Promise
     */
    Service.getApprovers = function(group, assignee) {
        var cacheApprovers = new Store.CacheFifo(Service.CacheNames.COMPLAINT_APPROVERS);
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
     * @methodOf services:Complaint.LookupService
     *
     * @description
     * Validate complaint approver data
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
