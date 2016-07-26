'use strict';

/**
 * @ngdoc service
 * @name services:Object.LockingService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object.locking.client.service.js services/object/object.locking.client.service.js}

 * Object.LockingService includes group of REST calls related to locking/unlocking objects.
 */
angular.module('services').factory('Object.LockingService', ['$resource', '$translate', 'UtilService',
    function ($resource, $translate, Util) {
        var Service = $resource('api/v1/service', {}, {

            /**
             * @ngdoc method
             * @name _lockObject
             * @methodOf services:Object.LockingService
             *
             * @description
             * Lock the object.
             *
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             *
             * @returns {Object} Object returned by $resource
             */
            _lockObject: {
                method: 'PUT',
                url: 'api/v1/plugin/:objectType/:objectId/lock'

            },
            /**
             * @ngdoc method
             * @name _unlockObject
             * @methodOf services:Object.LockingService
             *
             * @description
             * Lock the object.
             *
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             *
             * @returns {Object} Object returned by $resource
             */
            _unlockObject: {
                method: 'DELETE',
                url: 'api/v1/plugin/:objectType/:objectId/lock',
                transformResponse: function (value) {
                    return value;
                }
            }
        });

        /**
         * @ngdoc method
         * @name lockObject
         * @methodOf services:Object.LockingService
         *
         * @description
         * Lock the object.
         *
         * @param {String} params.objectType  Object type
         * @param {Number} params.objectId  Object ID
         * @param {String} params.lockType  Lock type
         *
         * @returns {Object} Object returned by $resource
         */
        Service.lockObject = function (objectId, objectType, lockType, lockInDB) {

            return Util.serviceCall({
                service: Service._lockObject
                , param: {
                    objectId: objectId,
                    objectType: objectType,
                    lockType: lockType,
                    lockInDB: lockInDB
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateObjectLocking(data)) {
                        return data;
                    }
                }
                , onInvalid: function (data) {
                    return data;
                }
            });
        };

        /**
         * @ngdoc method
         * @name unlockObject
         * @methodOf services:Object.LockingService
         *
         * @description
         * Unlock the object.
         *
         * @param {String} params.objectType  Object type
         * @param {Number} params.objectId  Object ID
         * @param {String} params.lockType  Lock type
         *
         * @returns {Object} Object returned by $resource
         */
        Service.unlockObject = function (objectId, objectType, lockType) {

            return Util.serviceCall({
                service: Service._unlockObject
                , param: {
                    objectId: objectId,
                    objectType: objectType,
                    lockType: lockType
                }
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateObjectLocking(data)) {
                        return data;
                    }
                }
                , onInvalid: function (data) {
                    return data;
                }
            });
        };

        Service.validateObjectLocking = function (data) {
            return !Util.isEmpty(data);
        };

        return Service;
    }
]);