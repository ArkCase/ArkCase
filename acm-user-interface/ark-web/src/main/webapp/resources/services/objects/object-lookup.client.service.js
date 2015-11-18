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
        });

        Service.SessionCacheNames = {
            PRIORITIES: "AcmPriorities"
            , OWNING_GROUPS: "AcmOwningGroups"
            , FILE_TYPES: "AcmFileTypes"
            , FORM_TYPES: "AcmFormTypes"
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

        return Service;
    }
]);