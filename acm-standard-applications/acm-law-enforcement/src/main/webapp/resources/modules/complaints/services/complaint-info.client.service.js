'use strict';

/**
 * @ngdoc service
 * @name services:Complaint.InfoService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/complaints/services/complaint-info.client.service.js modules/complaints/services/complaint-info.client.service.js}
 *
 * Complaint.InfoService provides functions for Complaint database data
 */
angular.module('services').factory('Complaint.InfoService', [ '$resource', '$translate', 'UtilService', 'CacheFactory', 'ObjectService', function($resource, $translate, Util, CacheFactory, ObjectService) {
    var complaintCache = CacheFactory(ObjectService.ObjectTypes.COMPLAINT, {
        maxAge: 1 * 60 * 1000, // Items added to this cache expire after 1 minute
        cacheFlushInterval: 60 * 60 * 1000, // This cache will clear itself every hour
        deleteOnExpire: 'aggressive', // Items will be deleted from this cache when they expire
        capacity: 1
    });
    var complaintGetUrl = 'api/latest/plugin/complaint/byId/';
    var complaintUrl = 'api/latest/plugin/complaint/';

    var Service = $resource('api/latest/plugin', {}, {

        /**
         * @ngdoc method
         * @name save
         * @methodOf services:Complaint.InfoService
         *
         * @description
         * Save complaint data
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Complaint ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/latest/plugin/complaint',
            cache: false
        },
        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Complaint.InfoService
         *
         * @description
         * Query complaint data from database.
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Object ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            url: complaintGetUrl + ':id',
            cache: complaintCache,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name get
         * @methodOf services:Complaint.InfoService
         *
         * @description
         * Query complaint data from database.
         *
         * @param {String} complaintNumber  complaintNumber
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        getByNumber: {
            method: 'GET',
            url: complaintUrl + "bynumber",
            cache: false,
            isArray: false
        },

        close: {
            method: 'POST',
            url: complaintUrl + "close/" + ':mode',
            cache: false
        }
    });

    /**
     * @ngdoc method
     * @name resetComplaintInfo
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Reset Complaint info
     *
     * @returns None
     */
    Service.resetComplaintInfo = function(complaintInfo) {
        if (complaintInfo && complaintInfo.complaintId) {
            complaintCache.remove(complaintGetUrl + complaintInfo.complaintId);
        }
    };

    /**
     * @ngdoc method
     * @name updateComplaintInfo
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Update complaint data in local cache. No REST call to backend.
     *
     * @param {Object} complaintInfo  Complaint data
     *
     * @returns {Object} Promise
     */
    Service.updateComplaintInfo = function(complaintInfo) {
        //TODO remove this method
    };

    /**
     * @ngdoc method
     * @name getComplaintInfo
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Query complaint data
     *
     * @param {Number} id  Complaint ID
     *
     * @returns {Object} Promise
     */
    Service.getComplaintInfo = function(id) {
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            onSuccess: function(data) {
                if (Service.validateComplaintInfo(data)) {
                    return data;
                }
            },
            onError: function(error) {
                MessageService.error(error.data);
                return error;
            }
        });
    };

    /**
     * @ngdoc method
     * @name getComplaintByNumber
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Query complaint data by number
     *
     * @param {String} complaintNumber complaintNumber
     *
     * @returns {Object} Promise
     */
    Service.getComplaintByNumber = function(complaintNumber) {
        return Util.serviceCall({
            service: Service.getByNumber,
            param: {
                complaintNumber: complaintNumber
            },
            onSuccess: function(data) {
                if (Service.validateComplaintInfo(data)) {
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name closeComplaint
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Close/Save complaint
     *
     * @param {String} mode mode
     *
     * @data {Object} data CloseComplaintRequest
     *
     * @returns {Object} Promise
     */
    Service.closeComplaint = function(mode, data) {
        return Util.serviceCall({
            service: Service.close,
            param: {
                mode: mode
            },
            data: data,
            onSuccess: function(data) {
                return data;
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveComplaintInfo
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Save complaint data
     *
     * @param {Object} complaintInfo  Complaint data
     *
     * @returns {Object} Promise
     */
    Service.saveComplaintInfo = function(complaintInfo) {
        if (!Service.validateComplaintInfo(complaintInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        //we need to make one of the fields is changed in order to be sure that update will be executed
        //if we change modified won't make any differences since is updated before update to database
        //but update will be trigger
        complaintInfo.modified = null;
        return Util.serviceCall({
            service: Service.save,
            data: JSOG.encode(complaintInfo),
            onSuccess: function(data) {
                if (Service.validateComplaintInfo(data)) {
                    complaintCache.put(complaintGetUrl + data.complaintId, data);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveComplaintInfoNewComplaint
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Save complaint data
     *
     * @param {Object} complaintInfo  Complaint data
     *
     * @returns {Object} Promise
     */
    Service.saveComplaintInfoNewComplaint = function(complaintInfo) {
        if (!Service.validateComplaintInfoNewComplaint(complaintInfo)) {
            return Util.errorPromise($translate.instant("common.service.error.invalidData"));
        }
        //we need to make one of the fields is changed in order to be sure that update will be executed
        //if we change modified won't make any differences since is updated before update to database
        //but update will be trigger
        complaintInfo.modified = null;
        return Util.serviceCall({
            service: Service.save,
            data: JSOG.encode(complaintInfo),
            onSuccess: function(data) {
                if (Service.validateComplaintInfo(data)) {
                    complaintCache.put(complaintGetUrl + data.complaintId, data);
                    return data;
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateComplaintInfo
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Validate complaint data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateComplaintInfo = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (0 >= Util.goodValue(data.complaintId, 0)) {
            return false;
        }
        if (Util.isEmpty(data.complaintNumber)) {
            return false;
        }
        if (!Util.isArray(data.childObjects)) {
            return false;
        }
        if (!Util.isArray(data.participants)) {
            return false;
        }
        if (!Util.isArray(data.personAssociations)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name validateComplaintInfoNewComplaint
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Validate complaint data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateComplaintInfoNewComplaint = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (data.complaintId) {
            return false;
        }
        if (data.participants && !Util.isArray(data.participants)) {
            return false;
        }
        if (!Util.isArray(data.personAssociations)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name validateReferenceRecord
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Validate complaint reference data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateReferenceRecord = function(data) {
        if (Util.isEmpty(data.associationType)) {
            return false;
        }
        if ("REFERENCE" != data.associationType) {
            return false;
        }
        if (Util.isEmpty(data.targetId)) {
            return false;
        }
        if (Util.isEmpty(data.targetName)) {
            return false;
        }
        if (Util.isEmpty(data.created)) {
            return false;
        }
        if (Util.isEmpty(data.creator)) {
            return false;
        }
        if (Util.isEmpty(data.status)) {
            return false;
        }
        return true;
    };

    /**
     * @ngdoc method
     * @name validateReferenceRecord
     * @methodOf services:Complaint.InfoService
     *
     * @description
     * Validate complaint reference data
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Boolean} Return true if data is valid
     */
    Service.validateLocation = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (Util.isEmpty(data.streetAddress)) {
            return false;
        }
        if (Util.isEmpty(data.type)) {
            return false;
        }
        if (Util.isEmpty(data.city)) {
            return false;
        }
        if (Util.isEmpty(data.state)) {
            return false;
        }
        if (Util.isEmpty(data.zip)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);
