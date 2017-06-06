'use strict';

/**
 * @ngdoc service
 * @name services:PersonAssociation.Service
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/common/services/person-association.client.service.js modules/common/services/person-association.client.service.js}
 *
 * PersonAssociation.Service provides functions for Person database data
 */
angular.module('services').factory('PersonAssociation.Service', ['$resource', '$translate', 'Acm.StoreService', 'UtilService',
    function ($resource, $translate, Store, Util) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name save
             * @methodOf services:PersonAssociation.Service
             *
             * @description
             * Save person association data
             *
             * @param {Object} params Map of input parameter.
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            save: {
                method: 'POST',
                url: 'api/latest/plugin/person-associations',
                cache: false
            },

            /**
             * @ngdoc method
             * @name get
             * @methodOf services:PersonAssociation.Service
             *
             * @description
             * Get person associations data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.personId  Person ID - parent associations for person
             * @param {String} params.parentType  Parent Type - filter by parent type (optional)
             * @param {Number} params.parentId  Parent ID - filter by parent id (optional)
             * @param {boolean} params.parentObjectsOnly  - display parent objects instead of person association (optional), false by default
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            get: {
                method: 'GET',
                url: 'api/latest/plugin/person-associations',
                params: {
                    'person-id': '@personId',
                    'parent-type': '@parentType',
                    'parent-id': '@parentId',
                    'parent-objects-only': '@parentObjectOnly'
                },
                cache: false
            }

        });

        /**
         * @ngdoc method
         * @name getPersonAssociations
         * @methodOf services:PersonAssociation.Service
         *
         * @description
         * Query person data
         *
         * @param {Number} personId  Person ID - parent associations for person
         * @param {String} parentType  Parent Type - filter by parent type (optional)
         * @param {Number} parentId  Parent ID - filter by parent id (optional)
         * @param {boolean} parentObjectsOnly  - display parent objects instead of person association (optional), false by default
         *
         * @returns {Object} Promise
         */
        Service.getPersonAssociations = function (personId, parentType, parentId, parentObjectOnly) {
            return Util.serviceCall({
                service: Service.get,
                data: {
                    personId: personId,
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
         * @name savePersonAssociation
         * @methodOf services:PersonAssociation.Service
         *
         * @description
         * Save person data
         *
         * @param {Object} personAssociation  Person data
         *
         * @returns {Object} Promise
         */
        Service.savePersonAssociation = function (personAssociation) {
            return Util.serviceCall({
                service: Service.save
                , param: {}
                , data: personAssociation
                , onSuccess: function (data) {
                    return data;
                }
            });
        };

        return Service;
    }
]);
