'use strict';

/**
 * @ngdoc service
 * @name services:PersonAssociation.Service
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/common/services/person-association.client.service.js modules/common/services/person-association.client.service.js}
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
                transformRequest: function (data, headersGetter) {
                    var encodedData = JSOG.encode(Util.omitNg(data));
                    return angular.toJson(encodedData);
                },
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
                url: 'api/latest/plugin/person-associations',
                params: {
                    'person-id': '@personId',
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
             * @methodOf services:PersonAssociation.Service
             *
             * @description
             * Get person associations data
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Person Association ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            get: {
                method: 'GET',
                url: 'api/latest/plugin/person-associations/:id',
                cache: false,
                isArray: false
            },

            /**
             * @ngdoc method
             * @name delete
             * @methodOf services:PersonAssociation.Service
             *
             * @description
             * Delete person association
             *
             * @param {Object} params Map of input parameter.
             * @param {Number} params.id  Person Association ID
             * @param {Function} onSuccess (Optional)Callback function of success query.
             * @param {Function} onError (Optional) Callback function when fail.
             *
             * @returns {Object} Object returned by $resource
             */
            delete: {
                method: 'DELETE',
                url: 'api/latest/plugin/person-associations/:id',
                cache: false,
                isArray: false
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
         * @param {Number} personId  Person ID
         * @param {String} parentType  Parent Type
         * @param {Number} start  used for paging, from which row to start
         * @param {Number} n  used for paging, how many rows to return
         * @param {String} sort for which field sorting should be done, default is id
         *
         * @returns {Object} Promise
         */
        Service.getPersonAssociations = function (personId, parentType, start, n, sort) {
            return Util.serviceCall({
                service: Service.list,
                data: {
                    personId: personId,
                    parentType: parentType,
                    start: start,
                    n: n,
                    sort: sort
                },
                onSuccess: function (data) {
                    return data;
                }
            });
        };

        /**
         * @ngdoc method
         * @name getPersonAssociation
         * @methodOf services:PersonAssociation.Service
         *
         * @description
         * Query person association data
         *
         * @param {Number} id  Person Association ID
         *
         * @returns {Object} Promise
         */
        Service.getPersonAssociation = function (id) {
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

        /**
         * @ngdoc method
         * @name deletePersonAssociationInfo
         * @methodOf services:PersonAssociation.Service
         *
         * @description
         * Delete person association
         *
         * @param {Number} id  Association ID
         *
         * @returns {Object} Promise
         */
        Service.deletePersonAssociationInfo = function (id) {
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
