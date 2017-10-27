'use strict';

/**
 * @ngdoc service
 * @name services:OrganizationAssociation.Service
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/common/services/organization-association.client.service.js modules/common/services/organization-association.client.service.js}
 *
 * OrganizationAssociation.Service provides functions for Organization database data
 */
angular.module('services').factory('OrganizationAssociation.Service', ['$resource', '$translate', 'Acm.StoreService', 'UtilService',
    function ($resource, $translate, Store, Util) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name save
             * @methodOf services:OrganizationAssociation.Service
             *
             * @description
             * Save organization association data
             *
             * @param {Object} params Map of input parameter.
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            save: {
                method: 'POST',
                url: 'api/latest/plugin/organization-associations',
                transformRequest: function (data, headersGetter) {
                    var encodedData = JSOG.encode(Util.omitNg(data));
                    return angular.toJson(encodedData);
                },
                cache: false
            },

            /**
             * @ngdoc method
             * @name get
             * @methodOf services:OrganizationAssociation.Service
             *
             * @description
             * Get organization associations data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.organizationId  Organization ID - parent associations for organization
             * @param {String} params.parentType  Parent Type - filter by parent type (optional)
             * @param {Number} params.start  start row
             * @param {Number} params.n  how many rows to return
             * @param {String} params.sort sort field
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            list: {
                method: 'GET',
                url: 'api/latest/plugin/organization-associations',
                params: {
                    'organization-id': '@organizationId',
                    'parent-type': '@parentType',
                    'start': '@start',
                    'n': '@n',
                    'sort': '@sort'
                },
                cache: false
            },
            /**
             * @ngdoc method
             * @name get
             * @methodOf services:OrganizationAssociation.Service
             *
             * @description
             * Get organization associations data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Organization Association ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            get: {
                method: 'GET',
                url: 'api/latest/plugin/organization-associations/:id',
                cache: false,
                isArray: false
            },

            /**
             * @ngdoc method
             * @name delete
             * @methodOf services:OrganizationAssociation.Service
             *
             * @description
             * Delete organization association
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Organization Association ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            delete: {
                method: 'DELETE',
                url: 'api/latest/plugin/organization-associations/:id',
                cache: false,
                isArray: false
            }
        });

        /**
         * @ngdoc method
         * @name getOrganizationAssociations
         * @methodOf services:OrganizationAssociation.Service
         *
         * @description
         * Query organization data
         *
         * @param {Number} organizationId  Organization ID - parent associations for organization
         * @param {String} parentType  Parent Type - filter by parent type (optional)
         * @param {Number} parentId  Parent ID - filter by parent id (optional)
         * @param {boolean} parentObjectsOnly  - display parent objects instead of organization association (optional), false by default
         *
         * @returns {Object} Promise
         */
        Service.getOrganizationAssociations = function (organizationId, parentType, parentId, parentObjectOnly) {
            return Util.serviceCall({
                service: Service.list,
                data: {
                    organizationId: organizationId,
                    parentType: parentType,
                    parentId: parentId,
                    parentObjectOnly: parentObjectOnly
                },
                onSuccess: function (data) {
                    return data;
                }
            });
        };

        /**
         * @ngdoc method
         * @name getOrganizationAssociation
         * @methodOf services:OrganizationAssociation.Service
         *
         * @description
         * Query organization association data
         *
         * @param {Number} id  Organization Association ID
         *
         * @returns {Object} Promise
         */
        Service.getOrganizationAssociation = function (id) {
            return Util.serviceCall({
                service: Service.get,
                param: {
                    id: id
                },
                onSuccess: function (data) {
                    return data;
                }
            });
        };

        /**
         * @ngdoc method
         * @name saveOrganizationAssociation
         * @methodOf services:OrganizationAssociation.Service
         *
         * @description
         * Save organization data
         *
         * @param {Object} organizationAssociation  Organization data
         *
         * @returns {Object} Promise
         */
        Service.saveOrganizationAssociation = function (organizationAssociation) {
            return Util.serviceCall({
                service: Service.save
                , param: {}
                , data: organizationAssociation
                , onSuccess: function (data) {
                    return data;
                }
            });
        };

        /**
         * @ngdoc method
         * @name deleteOrganizationAssociationInfo
         * @methodOf services:OrganizationAssociation.Service
         *
         * @description
         * Delete organization association
         *
         * @param {Number} id  Association ID
         *
         * @returns {Object} Promise
         */
        Service.deleteOrganizationAssociationInfo = function (id) {
            return Util.serviceCall({
                service: Service.delete
                , param: {
                    id: id
                }
            });
        };

        return Service;
    }
]);
