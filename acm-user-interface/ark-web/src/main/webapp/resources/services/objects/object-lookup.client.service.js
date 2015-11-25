'use strict';

/**
 * @ngdoc service
 * @name services:Object.LookupService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/objects/object-lookup.client.service.js services/objects/object-lookup.client.service.js}

 * LookupService contains functions to lookup data (typically static data).
 */
angular.module('services').factory('Object.LookupService', ['$resource', 'StoreService', 'UtilService', 'Object.ListService',
    function ($resource, Store, Util, ObjectListService) {
        var Service = $resource('proxy/arkcase/api/latest/plugin', {}, {
            /**
             * @ngdoc method
             * @name _getPriorities
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of priorities
             *
             * @returns {Array} An array returned by $resource
             */
            _getPriorities: {
                url: "proxy/arkcase/api/latest/plugin/complaint/priorities"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getOwningGroups
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of groups for object approval
             *
             * @returns {Object} Data returned by $resource
             */
            , _getOwningGroups: {
                url: "proxy/arkcase/api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc"
                , method: "GET"
                , cache: true
            }

            /**
             * @ngdoc method
             * @name _getFileTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of file types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getFileTypes: {
                url: "modules_config/config/modules/cases/resources/fileTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getFormTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of form types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getFormTypes: {
                url: "proxy/arkcase/api/latest/plugin/admin/plainforms/:objType"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getPersonTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of person types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getPersonTypes: {
                url: "proxy/arkcase/api/latest/plugin/person/types"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getParticipantTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of participant types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getParticipantTypes: {
                url: "modules_config/config/modules/cases/resources/participantTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true

            }

            /**
             * @ngdoc method
             * @name _getPersonTitles
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of person titles
             *
             * @returns {Object} An array returned by $resource
             */
            , _getPersonTitles: {
                url: "modules_config/config/modules/cases/resources/personTitles.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getContactMethodTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of contact method types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getContactMethodTypes: {
                url: "modules_config/config/modules/cases/resources/contactMethodTypes.json"
                , method: "GET"
                , cache: true
                , isArray : true
            }

            /**
             * @ngdoc method
             * @name _getOrganizationTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of organization types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getOrganizationTypes: {
                url: "modules_config/config/modules/cases/resources/organizationTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getAddressTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of address types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getAddressTypes: {
                url: "modules_config/config/modules/cases/resources/addressTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getAliasTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of alias types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getAliasTypes: {
                url: "modules_config/config/modules/cases/resources/aliasTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getSecurityTagTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of security tag types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getSecurityTagTypes: {
                url: "modules_config/config/modules/cases/resources/securityTagTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getObjectTypes
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of object types
             *
             * @returns {Object} An array returned by $resource
             */
            , _getObjectTypes: {
                url: "modules_config/config/modules/cases/resources/objectTypes.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name getCorrespondenceForms
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of correspondence forms
             *
             * @returns {Object} An array returned by $resource
             */
            , _getCorrespondenceForms: {
                url: "modules_config/config/modules/cases/resources/correspondenceForms.json"
                , method: "GET"
                , cache: true
                , isArray: true
            }
        });

        Service.SessionCacheNames = {
            PRIORITIES: "AcmPriorities"
            , OWNING_GROUPS: "AcmOwningGroups"
            , FILE_TYPES: "AcmFileTypes"
            , FORM_TYPES: "AcmFormTypes"
            , PERSON_TYPES: "AcmPersonTypes"
            , PARTICIPANT_TYPES: "AcmParticipantTypes"
            , PERSON_TITLES: "AcmPersonTitles"
            , CONTACT_METHOD_TYPES: "AcmContactMethodTypes"
            , ORGANIZATION_TYPES: "AcmOrganizationTypes"
            , ADDRESS_TYPES: "AcmAddressTypes"
            , ALIAS_TYPES: "AcmAliasTypes"
            , SECURITY_TAG_TYPES: "AcmSecurityTagTypes"
            , OBJECT_TYPES: "AcmObjectTypes"
            , CORRESPONDENCE_FORMS: "AcmCorrespondenceForms"
        };
        Service.CacheNames = {};

        /**
         * @ngdoc method
         * @name getPriorities
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of priorities
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getPriorities = function () {
            var cachePriorities = new Store.SessionData(Service.SessionCacheNames.PRIORITIES);
            var priorities = cachePriorities.get();
            return Util.serviceCall({
                service: Service._getPriorities
                , result: priorities
                , onSuccess: function (data) {
                    if (Service.validatePriorities(data)) {
                        cachePriorities.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validatePriorities
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of priorities data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePriorities = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name getOwningGroups
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of groups for object approval
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getOwningGroups = function () {
            var cacheGroups = new Store.SessionData(Service.SessionCacheNames.OWNING_GROUPS);
            var groups = cacheGroups.get();
            return Util.serviceCall({
                service: Service._getOwningGroups
                , result: groups
                , onSuccess: function (data) {
                    if (Service.validateOwningGroups(data)) {
                        groups = data.response.docs;
                        cacheGroups.set(groups);
                        return groups;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateOwningGroups
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of owning groups data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateOwningGroups = function (data) {
            if (!ObjectListService.validateSolrData(data)) {
                return false;
            }

            return true;
        };


        /**
         * @ngdoc method
         * @name getFileTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of file types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getFileTypes = function () {
            var cacheFileTypes = new Store.SessionData(Service.SessionCacheNames.FILE_TYPES);
            var fileTypes = cacheFileTypes.get();
            return Util.serviceCall({
                service: Service._getFileTypes
                , result: fileTypes
                , onSuccess: function (data) {
                    if (Service.validateFileTypes(data)) {
                        cacheFileTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateFileTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of file types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateFileTypes = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getFormTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of plain form types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getFormTypes = function () {
            var cacheFormTypes = new Store.SessionData(Service.SessionCacheNames.FORM_TYPES);
            var formTypes = cacheFormTypes.get();
            return Util.serviceCall({
                service: Service._getFormTypes
                , result: formTypes
                , onSuccess: function (data) {
                    if (Service.validatePlainForms(data)) {
                        var plainForms = data;
                        formTypes = [];
                        _.each(plainForms, function (plainForm) {
                            var formType = {};
                            formType.type = plainForm.key;
                            formType.label = Util.goodValue(plainForm.name);
                            formType.url = Util.goodValue(plainForm.url);
                            formType.urlParameters = Util.goodArray(plainForm.urlParameters);
                            formType.form = true;
                            formTypes.unshift(formType);
                        });
                        cacheFormTypes.set(formTypes);
                        return formTypes;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validatePlainForms
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of plain forms data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePlainForms = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validatePlainForm(data[i])) {
                    return false;
                }
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name validatePlainForm
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate a plain form data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePlainForm = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.key) && Util.isEmpty(data.type)) {  //different attribute name. service data use "key"; menu item use "type"
                return false;
            }
            if (Util.isEmpty(data.url)) {
                return false;
            }
            if (!Util.isArray(data.urlParameters)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name getPersonTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of person types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getPersonTypes = function () {
            var cachePersonTypes = new Store.SessionData(Service.SessionCacheNames.PERSON_TYPES);
            var personTypes = cachePersonTypes.get();
            return Util.serviceCall({
                service: Service._getPersonTypes
                , result: personTypes
                , onSuccess: function (data) {
                    if (Service.validatePersonTypes(data)) {
                        cachePersonTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validatePersonTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of person types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePersonTypes = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getParticipantTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of participant types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getParticipantTypes = function () {
            var cacheParticipantTypes = new Store.SessionData(Service.SessionCacheNames.PARTICIPANT_TYPES);
            var participantTypes = cacheParticipantTypes.get();
            return Util.serviceCall({
                service: Service._getParticipantTypes
                , result: participantTypes
                , onSuccess: function (data) {
                    if (Service.validateParticipantTypes(data)) {

                        participantTypes = [{type: "*", name: "*"}];
                        Util.forEachStripNg(data, function (v, k) {
                            participantTypes.push({type: k, name: v});
                        });

                        cacheParticipantTypes.set(participantTypes);
                        return participantTypes;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateParticipantTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of participant types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateParticipantTypes = function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getPersonTitles
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of person titles
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getPersonTitles = function () {
            var cachePersonTitles = new Store.SessionData(Service.SessionCacheNames.FILE_TYPES);
            var personTitles = cachePersonTitles.get();
            return Util.serviceCall({
                service: Service._getPersonTitles
                , result: personTitles
                , onSuccess: function (data) {
                    if (Service.validatePersonTitles(data)) {
                        cachePersonTitles.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validatePersonTitles
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of person titles data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validatePersonTitles = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getContactMethodTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of contact method types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getContactMethodTypes = function () {
            var cacheContactMethodTypes = new Store.SessionData(Service.SessionCacheNames.CONTACT_METHOD_TYPES);
            var contactMethodTypes = cacheContactMethodTypes.get();
            return Util.serviceCall({
                service: Service._getContactMethodTypes
                , result: contactMethodTypes
                , onSuccess: function (data) {
                    if (Service.validateContactMethodTypes(data)) {
                        cacheContactMethodTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateContactMethodTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of contact method types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateContactMethodTypes = function (data) {
            //if (!Util.isArray(data)) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getOrganizationTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of organization types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getOrganizationTypes = function () {
            var cacheOrganizationTypes = new Store.SessionData(Service.SessionCacheNames.ORGANIZATION_TYPES);
            var organizationTypes = cacheOrganizationTypes.get();
            return Util.serviceCall({
                service: Service._getOrganizationTypes
                , result: organizationTypes
                , onSuccess: function (data) {
                    if (Service.validateOrganizationTypes(data)) {
                        cacheOrganizationTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateOrganizationTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of organization types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateOrganizationTypes = function (data) {
            //if (!Util.isArray(data)) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getAddressTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of address types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getAddressTypes = function () {
            var cacheAddressTypes = new Store.SessionData(Service.SessionCacheNames.ADDRESS_TYPES);
            var addressTypes = cacheAddressTypes.get();
            return Util.serviceCall({
                service: Service._getAddressTypes
                , result: addressTypes
                , onSuccess: function (data) {
                    if (Service.validateAddressTypes(data)) {
                        cacheAddressTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateAddressTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of address types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateAddressTypes = function (data) {
            //if (!Util.isArray(data)) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getAliasTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of alias types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getAliasTypes = function () {
            var cacheAliasTypes = new Store.SessionData(Service.SessionCacheNames.ALIAS_TYPES);
            var aliasTypes = cacheAliasTypes.get();
            return Util.serviceCall({
                service: Service._getAliasTypes
                , result: aliasTypes
                , onSuccess: function (data) {
                    if (Service.validateAliasTypes(data)) {
                        cacheAliasTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateAliasTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of alias types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateAliasTypes = function (data) {
            //if (!Util.isArray(data)) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getSecurityTagTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of security tag types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getSecurityTagTypes = function () {
            var cacheSecurityTagTypes = new Store.SessionData(Service.SessionCacheNames.SECURITY_TAG_TYPES);
            var securityTagTypes = cacheSecurityTagTypes.get();
            return Util.serviceCall({
                service: Service._getSecurityTagTypes
                , result: securityTagTypes
                , onSuccess: function (data) {
                    if (Service.validateSecurityTagTypes(data)) {
                        cacheSecurityTagTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateSecurityTagTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of security tag types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateSecurityTagTypes = function (data) {
            //if (!Util.isArray(data)) {
            if (Util.isEmpty(data)) {
                return false;
            }
            return true;
        };

        /**
         * @ngdoc method
         * @name getObjectTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of object types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getObjectTypes = function () {
            var cacheObjectTypes = new Store.SessionData(Service.SessionCacheNames.OBJECT_TYPES);
            var objectTypes = cacheObjectTypes.get();
            return Util.serviceCall({
                service: Service._getObjectTypes
                , result: objectTypes
                , onSuccess: function (data) {
                    if (Service.validateObjectTypes(data)) {
                        cacheObjectTypes.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateObjectTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of object types data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateObjectTypes = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };


        /**
         * @ngdoc method
         * @name getCorrespondenceForms
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of correspondence forms
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getCorrespondenceForms = function () {
            var cacheCorrespondenceForms = new Store.SessionData(Service.SessionCacheNames.CORRESPONDENCE_FORMS);
            var correspondenceForms = cacheCorrespondenceForms.get();
            return Util.serviceCall({
                service: Service._getCorrespondenceForms
                , result: correspondenceForms
                , onSuccess: function (data) {
                    if (Service.validateCorrespondenceForms(data)) {
                        cacheCorrespondenceForms.set(data);
                        return data;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateCorrespondenceForms
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of correspondence forms
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateCorrespondenceForms = function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        };

        return Service;
    }
]);