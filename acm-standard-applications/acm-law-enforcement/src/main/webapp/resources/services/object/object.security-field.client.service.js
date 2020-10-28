'use strict';

/**
 * @ngdoc service
 * @name services:Object.SecurityFieldService
 *
 * @description
 *
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-security-field.client.service.js services/object/object-security-field.client.service.js}

 * Object.SecurityFieldsService includes group of REST calls related to security fields.
 */
angular.module('services').factory('Object.SecurityFieldService', [ '$resource', '$translate', 'UtilService', function($resource, $translate, Util) {
    var Service = $resource('api/v1/service', {}, {

        /**
         * @ngdoc method
         * @name update
         * @methodOf services:Object.SecurityFieldService
         *
         * @description
         * Change Security Field value.
         *
         * @param {String} params.securityFieldValue  Security Field Value [Open | Restricted]
         * @param {String} params.objectType  Object type
         * @param {String} params.objectId  Object ID
         * @param {Function} onSuccess (Optional)Callback function of success query
         * @param {Function} onError (Optional) Callback function when fail
         *
         * @returns {Object} Object returned by $resource
         */
        save: {
            method: 'POST',
            url: 'api/v1/service/:objectType/:objectId/security-field/:securityFieldValue',
            cache: false
        }
    });

    /**
     * @ngdoc method
     * @name updateSecurityField
     * @methodOf services:Object.SecurityFieldService
     *
     * @description
     * Save security field for an object.
     *
     * @param {String} securityFieldValue  Security Field Value [Open | Restricted]
     * @param {String} objectType  Object Type
     * @param {String} objectId   Object ID
     *
     * @returns {Object} Promise
     */
    Service.updateSecurityField = function(securityFieldValue, objectType, objectId) {

        return Util.serviceCall({
            service: Service.save,
            param: {
                securityFieldValue: securityFieldValue,
                objectType: objectType.toLowerCase(),
                objectId: objectId
            },
            data: {},
            onSuccess: function(data) {
                if (Service.validateSecurityField(data)) {
                    return data;
                }
            }
        })
    };

    /**
     * @ngdoc method
     * @name validateSecurityField
     * @methodOf services:Object.SecurityFieldService
     *
     * @description
     * Validate SecurityField.
     *
     * @param {Object} data  Data to be validated
     *
     * @returns {Object} Promise
     */
    Service.validateSecurityField = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (Util.isEmpty(data.securityField)) {
            return false;
        }
        return true;
    };

    return Service;
} ]);