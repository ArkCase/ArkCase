'use strict';

/**
 * @ngdoc service
 * @name services:Object.OrganizationService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-organization.client.service.js services/object/object-organization.client.service.js}

 * Object.OrganizationService includes group of REST calls related to organization association.
 */
angular.module('services').factory('Object.OrganizationService', ['$resource', 'Acm.StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _addOrganizationAssociation
             * @methodOf services:Object.OrganizationService
             *
             * @description
             * Add a organization to association
             *
             * @param {Object} data Organization association data
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _addOrganizationAssociation: {
                method: 'POST',
                url: 'api/latest/plugin/organizationAssociation',
                cache: false
            }

            /**
             * @ngdoc method
             * @name _deleteOrganizationAssociation
             * @methodOf services:Object.OrganizationService
             *
             * @description
             * Delete a organization association
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.organizationAssociationId  Organization association ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _deleteOrganizationAssociation: {
                method: 'DELETE',
                url: 'api/latest/plugin/organizationAssociation/delete/:organizationAssociationId',
                cache: false
            }

            , _getOrganizationCases: {
                method: 'GET',
                url: 'api/latest/plugin/organizations/:id/associations/CASE_FILE',
                cache: false
            }

            , _getOrganizationComplaints: {
                method: 'GET',
                url: 'api/latest/plugin/organizations/:id/associations/COMPLAINT',
                cache: false
            }
        });

        Service.SessionCacheNames = {
            "ORGANIZATION_CASES_DATA": "OrganizationCasesData",
            "ORGANIZATION_COMPLAINTS_DATA": "OrganizationComplaintsData"
        };

        /**
         * @ngdoc method
         * @name addOrganizationAssociation
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Add a organization to association
         *
         * @param {Object} organizationAssociation  Organization Association data
         *
         * @returns {Object} Promise
         */
        Service.addOrganizationAssociation = function (organizationAssociation) {
            return Util.serviceCall({
                service: Service._addOrganizationAssociation
                , data: organizationAssociation
                , onSuccess: function (data) {
                    if (Service.validateOrganizationAssociation(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name deleteOrganizationAssociation
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Delete a organization association
         *
         * @param {Number} organizationAssociationId  Organization associationId ID
         *
         * @returns {Object} Promise
         */
        Service.deleteOrganizationAssociation = function (organizationAssociationId) {
            return Util.serviceCall({
                service: Service._deleteOrganizationAssociation
                , param: {organizationAssociationId: organizationAssociationId}
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateDeletedOrganizationAssociation(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name getOrganizationCases
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Query cases data for an object.
         *
         * @param {Number} id  Object ID
         *
         * @returns {Object} Promise
         */
        Service.getOrganizationCases = function (id) {
            var cacheData = new Store.CacheFifo(Service.SessionCacheNames.ORGANIZATION_CASES_DATA);
            var cacheKey = id;
            var casesData = cacheData.get(cacheKey);

            return Util.serviceCall({
                service: Service._getOrganizationCases
                , param: {
                    id: id
                }
                , result: casesData
                , onSuccess: function (data) {
                    if (Service.validateOrganizationCasesData(data)) {
                        casesData = data;
                        cacheData.put(cacheKey, data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name getOrganizationComplaints
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Query complaints data for an object.
         *
         * @param {Number} id  Object ID
         *
         * @returns {Object} Promise
         */
        Service.getOrganizationComplaints = function (id) {
            var cacheData = new Store.CacheFifo(Service.SessionCacheNames.ORGANIZATION_COMPLAINTS_DATA);
            var cacheKey = id;
            var complaintsData = cacheData.get(cacheKey);

            return Util.serviceCall({
                service: Service._getOrganizationComplaints
                , param: {
                    id: id
                }
                , result: complaintsData
                , onSuccess: function (data) {
                    if (Service.validateOrganizationComplaintsData(data)) {
                        complaintsData = data;
                        cacheData.put(cacheKey, data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateOrganizationAssociations
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Validate list of organization associations
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateOrganizationAssociations = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateOrganizationAssociation(data[i])) {
                    return false;
                }
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateOrganizationAssociation
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Validate organization association data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateOrganizationAssociation = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.organization)) {
                return false;
            }
            if (!Util.isArray(data.organization.contactMethods)) {
                return false;
            }
            if (!Util.isArray(data.organization.addresses)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateDeletedOrganizationAssociation
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Validate organization association data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateDeletedOrganizationAssociation = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedOrganizationAssociationId)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateCasesData
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Validate list of cases
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateOrganizationCasesData = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateOrganizationComplaintsData
         * @methodOf services:Object.OrganizationService
         *
         * @description
         * Validate list of cases
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateOrganizationComplaintsData = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };


        return Service;
    }
]);
