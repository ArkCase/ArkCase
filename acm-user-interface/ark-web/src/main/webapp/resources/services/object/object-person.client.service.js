'use strict';

/**
 * @ngdoc service
 * @name services:Object.PersonService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/object/object-person.client.service.js services/object/object-person.client.service.js}

 * Object.PersonService includes group of REST calls related to person association.
 */
angular.module('services').factory('Object.PersonService', ['$resource', 'StoreService', 'UtilService',
    function ($resource, Store, Util) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _addPersonAssociation
             * @methodOf services:Object.PersonService
             *
             * @description
             * Add a person to association
             *
             * @param {Object} data Person association data
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            _addPersonAssociation: {
                method: 'POST',
                url: 'proxy/arkcase/api/latest/plugin/personAssociation',
                cache: false
            }

            /**
             * @ngdoc method
             * @name _deletePersonAssociation
             * @methodOf services:Object.PersonService
             *
             * @description
             * Delete a person association
             *
             * @param {Object} params Map of input parameter
             * @param {String} params.personAssociationId  Person association ID
             * @param {Function} onSuccess (Optional)Callback function of success query
             * @param {Function} onError (Optional) Callback function when fail
             *
             * @returns {Object} Object returned by $resource
             */
            , _deletePersonAssociation: {
                method: 'DELETE',
                url: 'proxy/arkcase/api/latest/plugin/personAssociation/delete/:personAssociationId',
                cache: false
            }
        });


        /**
         * @ngdoc method
         * @name addPersonAssociation
         * @methodOf services:Object.PersonService
         *
         * @description
         * Add a person to association
         *
         * @param {Object} personAssociation  Person Association data
         *
         * @returns {Object} Promise
         */
        Service.addPersonAssociation = function (personAssociation) {
            return Util.serviceCall({
                service: Service._addPersonAssociation
                , data: personAssociation
                , onSuccess: function (data) {
                    if (Service.validatePersonAssociation(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name deletePersonAssociation
         * @methodOf services:Object.PersonService
         *
         * @description
         * Delete a person association
         *
         * @param {Number} personAssociationId  Person associationId ID
         *
         * @returns {Object} Promise
         */
        Service.deletePersonAssociation = function (personAssociationId) {
            return Util.serviceCall({
                service: Service._deletePersonAssociation
                , param: {personAssociationId: personAssociationId}
                , data: {}
                , onSuccess: function (data) {
                    if (Service.validateDeletedPersonAssociation(data)) {
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validatePersonAssociations
         * @methodOf services:Object.PersonService
         *
         * @description
         * Validate list of person associations
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePersonAssociations = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validatePersonAssociation(data[i])) {
                    return false;
                }
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validatePersonAssociation
         * @methodOf services:Object.PersonService
         *
         * @description
         * Validate person association data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePersonAssociation = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.person)) {
                return false;
            }
            if (!Util.isArray(data.person.contactMethods)) {
                return false;
            }
            if (!Util.isArray(data.person.addresses)) {
                return false;
            }
            if (!Util.isArray(data.person.securityTags)) {
                return false;
            }
            if (!Util.isArray(data.person.personAliases)) {
                return false;
            }
            if (!Util.isArray(data.person.organizations)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validateDeletedPersonAssociation
         * @methodOf services:Object.PersonService
         *
         * @description
         * Validate person association data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateDeletedPersonAssociation = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedPersonAssociationId)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);
