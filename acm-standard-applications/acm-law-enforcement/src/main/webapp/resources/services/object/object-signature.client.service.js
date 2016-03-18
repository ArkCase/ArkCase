'use strict';

/**
 * @ngdoc service
 * @name services:Object.SignatureService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-signature.client.service.js services/object/object-signature.client.service.js}

 * Object.SignatureService includes group of REST calls related to signature.
 */
angular.module('services').factory('Object.SignatureService', ['$resource', 'StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name findSignatures
             * @methodOf services:Object.SignatureService
             *
             * @description
             * Find list of signatures for an object
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.objectType  Object type
             * @param {Number} params.objectId  Object ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _findSignatures: {
                method: 'GET',
                url: 'api/latest/plugin/signature/find/:objectType/:objectId',
                cache: false,
                isArray: true
            }

        });


        Service.SessionCacheNames = {};
        Service.CacheNames = {
            SIGNATURES: "Signatures"
        };


        /**
         * @ngdoc method
         * @name findSignatures
         * @methodOf services:Object.SignatureService
         *
         * @description
         * Query list of notes of an object
         *
         * @param {String} objectType  Object type
         * @param {Number} objectId  Object ID
         *
         * @returns {Object} Promise
         */
        Service.findSignatures = function (objectType, objectId) {
            var cacheSignatures = new Store.CacheFifo(Service.CacheNames.SIGNATURES);
            var cacheKey = objectType + "." + objectId;
            var signatures = cacheSignatures.get(cacheKey);
            return Util.serviceCall({
                service: Service._findSignatures
                , param: {
                    objectType: objectType
                    , objectId: objectId
                }
                , result: signatures
                , onSuccess: function (data) {
                    if (Service.validateSignatures(data)) {
                        signatures = data;
                        cacheSignatures.put(cacheKey, signatures);
                        return signatures;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateSignatures
         * @methodOf services:Object.SignatureService
         *
         * @description
         * Validate list of signature data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSignatures = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };


        return Service;
    }
]);
