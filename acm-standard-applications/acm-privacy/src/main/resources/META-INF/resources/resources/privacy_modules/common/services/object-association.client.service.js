'use strict';

/**
 * @ngdoc service
 * @name services:ObjectAssociation.Service
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/modules/common/services/object-association.client.service.js modules/common/services/object-association.client.service.js}
 *
 * ObjectAssociation.Service provides functions for Object database data
 */
angular.module('services').factory('ObjectAssociation.Service', [ '$resource', '$translate', 'Acm.StoreService', 'UtilService', function($resource, $translate, Store, Util) {
    var Service = $resource('api/latest/plugin', {}, {
        /**
         * @ngdoc method
         * @name save
         * @methodOf services:ObjectAssociation.Service
         *
         * @description
         * Save object association data
         *
         * @param {Object} params Map of input parameter.
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/latest/service/objectassociations',
            transformRequest: function(data, headersGetter) {
                var encodedData = JSOG.encode(Util.omitNg(data));
                return angular.toJson(encodedData);
            },
            cache: false
        },

        /**
         * @ngdoc method
         * @name list
         * @methodOf services:ObjectAssociation.Service
         *
         * @description
         * Get object associations data
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.parentId  Parent ID - owner of the association
         * @param {String} params.parentType  Parent Type - owner of the association
         * @param {String} params.targetType  - target object
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        list: {
            method: 'GET',
            url: 'api/latest/service/objectassociations',
            params: {
                'parent-type': '@parentType',
                'parent-id': '@parentId',
                'target-type': '@targetType'
            },
            cache: false
        },

        /**
         * @ngdoc method
         * @name get
         * @methodOf services:ObjectAssociation.Service
         *
         * @description
         * Get object association data
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Association ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        get: {
            method: 'GET',
            url: 'api/latest/service/objectassociations/:id',
            cache: false,
            isArray: false
        },

        /**
         * @ngdoc method
         * @name delete
         * @methodOf services:ObjectAssociation.Service
         *
         * @description
         * Delete object association
         *
         * @param {Object} params Map of input parameter.
         * @param {Number} params.id  Association ID
         * @param {Function} onSuccess (Optional)Callback function of success query.
         * @param {Function} onError (Optional) Callback function when fail.
         *
         * @returns {Object} Object returned by $resource
         */
        _delete: {
            method: 'DELETE',
            url: 'api/latest/service/objectassociations/:id',
            cache: false,
            isArray: false
        }

    });

    /**
     * @ngdoc method
     * @name getObjectAssociations
     * @methodOf services:ObjectAssociation.Service
     *
     * @description
     * Query object data
     *
     * @param {Number} parentId  Parent ID - owner of the association
     * @param {String} parentType  Parent Type - owner of the association
     * @param {String} targetType  - Target Type - target object type
     *
     * @returns {Object} Promise
     */
    Service.getObjectAssociations = function(parentId, parentType, targetType) {
        return Util.serviceCall({
            service: Service.list,
            data: {
                parentType: parentType,
                parentId: parentId,
                targetType: targetType
            },
            onSuccess: function(data) {
                return data;
            }
        });
    };

    /**
     * @ngdoc method
     * @name saveObjectAssociation
     * @methodOf services:ObjectAssociation.Service
     *
     * @description
     * Save object data
     *
     * @param {Object} objectAssociation  Object data
     *
     * @returns {Object} Promise
     */
    Service.saveObjectAssociation = function(objectAssociation) {
        return Util.serviceCall({
            service: Service.save,
            param: {},
            data: objectAssociation,
            onSuccess: function(data) {
                return data;
            }
        });
    };

    Service.SessionCacheNames = {};
    Service.CacheNames = {
        OBJECT_ASSOCIATION_INFO: "ObjectAssociationInfo"
    };

    /**
     * @ngdoc method
     * @name getAssociationInfo
     * @methodOf services:ObjectAssociation.Service
     *
     * @description
     * Query association data
     *
     * @param {Number} id  Association ID
     *
     * @returns {Object} Promise
     */
    Service.getAssociationInfo = function(id) {
        var cacheAssociationInfo = new Store.CacheFifo(Service.CacheNames.OBJECT_ASSOCIATION_INFO);
        var associationInfo = cacheAssociationInfo.get(id);
        return Util.serviceCall({
            service: Service.get,
            param: {
                id: id
            },
            result: associationInfo,
            onSuccess: function(data) {
                cacheAssociationInfo.put(id, data);
                return data;
            }
        });
    };

    /**
     * @ngdoc method
     * @name deleteAssociationInfo
     * @methodOf services:ObjectAssociation.Service
     *
     * @description
     * Delete object association
     *
     * @param {Number} id  Association ID
     *
     * @returns {Object} Promise
     */
    Service.deleteAssociationInfo = function(id) {
        return Util.serviceCall({
            service: Service._delete,
            param: {
                id: id
            }
        });
    };

    /**
     * @ngdoc method
     * @name deleteAssociationInfo
     * @methodOf services:ObjectAssociation.Service
     *
     * @description
     * Delete object association
     *
     * @param {Number} id  Association ID
     *
     * @returns {Object} Promise
     */
    Service.createAssociationInfo = function(parentId, parentType, parentTitle, parentName, targetId, targetType, targetTitle, targetName, associationType, inverseAssociationType) {
        var association = {
            parentId: parentId,
            parentType: parentType,
            associationType: associationType,
            parentTitle: parentTitle,
            parentName: parentName,
            targetId: targetId,
            targetType: targetType,
            targetTitle: targetTitle,
            targetName: targetName
        };

        if (inverseAssociationType) {
            var inverseAssociation = {
                parentId: targetId,
                parentType: targetType,
                parentTitle: targetTitle,
                parentName: targetName,
                associationType: inverseAssociationType,
                targetId: parentId,
                targetType: parentType,
                targetTitle: parentTitle,
                targetName: parentName,
                inverseAssociation: association
            };
            association.inverseAssociation = inverseAssociation;
            return association;
        }
    };

    return Service;
} ]);
