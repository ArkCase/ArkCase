'use strict';

/**
 * @ngdoc service
 * @name services:Complaint.InfoService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/complaints/services/complaint-info.client.service.js modules/complaints/services/complaint-info.client.service.js}
 *
 * Complaint.InfoService provides functions for Complaint database data
 */
angular.module('services').factory('Complaint.InfoService', ['$resource', '$translate', 'Acm.StoreService', 'UtilService', 'Object.InfoService',
    function ($resource, $translate, Store, Util, ObjectInfoService) {
        var Service = $resource('api/latest/plugin', {}, {
            ///**
            // * ngdoc method
            // * name get
            // * methodOf services:Complaint.InfoService
            // *
            // * @description
            // * Query complaint data
            // *
            // * @param {Object} params Map of input parameter.
            // * @param {Number} params.id  Complaint ID
            // * @param {Function} onSuccess (Optional)Callback function of success query.
            // * @param {Function} onError (Optional) Callback function when fail.
            // *
            // * @returns {Object} Object returned by $resource
            // */
            //get: {
            //    method: 'GET',
            //    url: 'api/latest/plugin/complaint/byId/:id',
            //    cache: false,
            //    isArray: false
            //}

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
                url: 'api/latest/plugin/complaint/save/:id',
                cache: false
            }
        });

        Service.SessionCacheNames = {};
        Service.CacheNames = {
            COMPLAINT_INFO: "ComplaintInfo"
        };

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
        Service.resetComplaintInfo = function () {
            var cacheInfo = new Store.CacheFifo(Service.CacheNames.COMPLAINT_INFO);
            cacheInfo.reset();
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
        Service.updateComplaintInfo = function (complaintInfo) {
            if (Service.validateComplaintInfo(complaintInfo)) {
                var cacheComplaintInfo = new Store.CacheFifo(Service.CacheNames.COMPLAINT_INFO);
                cacheComplaintInfo.put(complaintInfo.id, complaintInfo);
            }
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
        Service.getComplaintInfo = function (id) {
            var cacheComplaintInfo = new Store.CacheFifo(Service.CacheNames.COMPLAINT_INFO);
            var complaintInfo = cacheComplaintInfo.get(id);
            return Util.serviceCall({
                service: ObjectInfoService.get
                , param: {type: "complaint", id: id}
                , result: complaintInfo
                , onSuccess: function (data) {
                    if (Service.validateComplaintInfo(data)) {
                        cacheComplaintInfo.put(id, data);
                        return data;
                    }
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
        Service.saveComplaintInfo = function (complaintInfo) {
            if (!Service.validateComplaintInfo(complaintInfo)) {
                return Util.errorPromise($translate.instant("common.service.error.invalidData"));
            }
            return Util.serviceCall({
                service: ObjectInfoService.save
                , param: {type: "complaint"}
                , data: complaintInfo
                , onSuccess: function (data) {
                    if (Service.validateComplaintInfo(data)) {
                        var complaintInfo = data;
                        var cacheComplaintInfo = new Store.CacheFifo(Service.CacheNames.COMPLAINT_INFO);
                        cacheComplaintInfo.put(complaintInfo.complaintId, complaintInfo);
                        return complaintInfo;
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
        Service.validateComplaintInfo = function (data) {
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
        Service.validateReferenceRecord = function (data) {
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
        Service.validateLocation = function (data) {
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
    }
]);
