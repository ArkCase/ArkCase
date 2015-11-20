'use strict';

/**
 * @ngdoc service
 * @name services:Complaint.LookupService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/modules/complaints/services/complaint-lookup.client.service.js modules/complaints/services/complaint-lookup.client.service.js}
 *
 * Complaint.LookupService provides functions for Complaint database data
 */
angular.module('services').factory('Complaint.LookupService', ['$resource', '$translate', 'StoreService', 'UtilService', 'Object.LookupService',
    function ($resource, $translate, Store, Util, ObjectLookupService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * ngdoc method
             * name _getComplaintTypes
             * methodOf services:Complaint.LookupService
             *
             * @description
             * Query list of complaint types
             *
             * @returns {Object} Object returned by $resource
             */
            _getComplaintTypes: {
                url: 'proxy/arkcase/api/latest/plugin/complaint/types'
                , cache: true
                , isArray: true
            }
        });

        Service.SessionCacheNames = {
            COMPLAINT_TYPES: "AcmComplaintTypes"
        };
        Service.CacheNames = {};

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
        Service.getComplaintTypes = function () {
            var cacheComplaintTypes = new Store.CacheFifo(Service.CacheNames.COMPLAINT_TYPES);
            var complaintTypes = cacheComplaintTypes.get();
            return Util.serviceCall({
                service: Service._getComplaintTypes
                , result: complaintTypes
                , onSuccess: function (data) {
                    if (Service.validateComplaintTypes(data)) {
                        cacheComplaintTypes.put(data);
                        return data;
                    }
                }
            });
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
        Service.validateComplaintTypes = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
