'use strict';

/**
 * @ngdoc service
 * @name services:Object.LookupService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-lookup.client.service.js services/object/object-lookup.client.service.js}

 * LookupService contains functions to lookup data (typically static data).
 */

angular.module('services').factory('Object.LookupService', ['$q', '$resource', 'Acm.StoreService', 'UtilService'
    , 'LookupService', 'SearchService',
    function ($q, $resource, Store, Util
        , LookupService, SearchService) {

        var Service = $resource('api/latest/plugin', {}, {
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
                url: "api/latest/plugin/complaint/priorities"
                , method: "GET"
                , cache: false
                , isArray: true
            }

            /**
             * @ngdoc method
             * @name _getGroups
             * @methodOf services:Object.LookupService
             *
             * @description
             * Query list of groups for object approval
             *
             * @returns {Object} Data returned by $resource
             */
            , _getGroups: {
                url: "api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc"
                , method: "GET"
                , cache: false
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
                url: "api/latest/plugin/admin/plainforms/:objType"
                , method: "GET"
                , cache: false
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
                url: "api/latest/plugin/person/types"
                , method: "GET"
                , cache: false
                , isArray: true
            }

        });

        Service.SessionCacheNames = {
            PRIORITIES: "AcmPriorities"
            , OWNING_GROUPS: "AcmOwningGroups"
            , FORM_TYPE_MAP: "AcmFormTypeMap"
            , PERSON_TYPES: "AcmPersonTypes"
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
         * @name getGroups
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of groups for object approval
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getGroups = function () {
            var cacheGroups = new Store.SessionData(Service.SessionCacheNames.OWNING_GROUPS);
            var groups = cacheGroups.get();
            return Util.serviceCall({
                service: Service._getGroups
                , result: groups
                , onSuccess: function (data) {
                    if (Service.validateGroups(data)) {
                        groups = data.response.docs;
                        cacheGroups.set(groups);
                        return groups;
                    }
                }
            });
        };

        /**
         * @ngdoc method
         * @name validateGroups
         * @methodOf services:Object.LookupService
         *
         * @description
         * Validate list of owning groups data
         *
         * @param {Object} data  Data to be validated
         *
         * @returns {Boolean} Return true if data is valid
         */
        Service.validateGroups = function (data) {
            if (!SearchService.validateSolrData(data)) {
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
         * @param {String} objType  Object type
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getFormTypes = function (objType) {
            var cacheFormTypeMap = new Store.SessionData(Service.SessionCacheNames.FORM_TYPE_MAP);
            var formTypeMap = cacheFormTypeMap.get();
            var formTypes = Util.goodMapValue(formTypeMap, objType, null);

            return Util.serviceCall({
                service: Service._getFormTypes
                , param: {objType: objType}
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

                        formTypeMap = formTypeMap || {};
                        formTypeMap[objType] = formTypes;
                        cacheFormTypeMap.set(formTypeMap);
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
         * @name getFileTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of file types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getFileTypes = function () {
            return LookupService.getLookup("fileTypes");
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
            return LookupService.getLookup("participantTypes");
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
            return LookupService.getLookup("personTitles");
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
            return LookupService.getLookup("contactMethodTypes");
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
            return LookupService.getLookup("organizationTypes");
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
            return LookupService.getLookup("addressTypes");
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
            return LookupService.getLookup("aliasTypes");
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
            return LookupService.getLookup("securityTagTypes");
        };

        /**
         * @ngdoc method
         * @name getPersonRelationTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of person relation types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getPersonRelationTypes = function () {
            return LookupService.getLookup("personRelationTypes");
        };

        /**
         * @ngdoc method
         * @name getOrganizationRelationTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of person relation types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getOrganizationRelationTypes = function () {
            return LookupService.getLookup("organizationRelationTypes");
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
            return LookupService.getLookup("objectTypes");
        };

        /**
         * @ngdoc method
         * @name getCaseFileCorrespondenceForms
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of correspondence forms for use in the Case File module
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getCaseFileCorrespondenceForms = function () {
            var df = $q.defer();
            var forms = LookupService.getLookup("caseCorrespondenceForms");
            forms.then(function (forms) {
                    var activated = _.filter(forms, function (form) {
                        return form.activated == true;
                    });
                    df.resolve(activated);
                },
                function (err) {
                    df.reject(err);
                });
            return df.promise;
        };

        /**
         * @ngdoc method
         * @name getComplaintCorrespondenceForms
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of correspondence forms for use in the Complaints module
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getComplaintCorrespondenceForms = function () {
            var df = $q.defer();
            var forms = LookupService.getLookup("complaintCorrespondenceForms");
            forms.then(function (forms) {
                    var activated = _.filter(forms, function (form) {
                        return form.activated == true;
                    });
                    df.resolve(activated);
                },
                function (err) {
                    df.reject(err);
                });
            return df.promise;
        };

        /**
         * @ngdoc method
         * @name getSecurityFieldTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of object types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getSecurityFieldTypes = function () {
            var data = LookupService.getLookup("securityFieldTypes");
            return data;
        };

        /**
         * @ngdoc method
         * @name getIdentificationTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of identification types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getIdentificationTypes = function () {
            return LookupService.getLookup("identificationTypes");
        };

        /**
         * @ngdoc method
         * @name getOrganizationIdTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of identification types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getOrganizationIdTypes = function () {
            return LookupService.getLookup("organizationIdTypes");
        };


        /**
         * @ngdoc method
         * @name getDBAsTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of identification types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getDBAsTypes = function () {
            return LookupService.getLookup("dbasTypes");
        };

        /**
         * @ngdoc method
         * @name getCountries
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of countries
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getCountries = function () {
            return LookupService.getLookup("countries");
        };

        /**
         * @ngdoc method
         * @name getPersonOrganizationRelationTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of person-organization relation types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getPersonOrganizationRelationTypes = function () {
            return LookupService.getLookup("personOrganizationRelationTypes");
        };

        /**
         * @ngdoc method
         * @name getOrganizationPersonRelationTypes
         * @methodOf services:Object.LookupService
         *
         * @description
         * Query list of organization-person relation types
         *
         * @returns {Object} An array returned by $resource
         */
        Service.getOrganizationPersonRelationTypes = function () {
            return LookupService.getLookup("organizationPersonRelationTypes");
        };

        Service.getLookupsDefs = function () {
            var lookupsDefs = [];
            for (var i = 0, len = lookups.standardLookup.length; i < len; i++) {
                lookupsDefs.push({'name' : Object.keys(lookups.standardLookup[i])[0], 'lookupType' : 'standardLookup'});
            }
            for (var i = 0, len = lookups.subLookup.length; i < len; i++) {
                lookupsDefs.push({'name' : Object.keys(lookups.subLookup[i])[0], 'lookupType' : 'subLookup'});
            }
            for (var i = 0, len = lookups.inverseValuesLookup.length; i < len; i++) {
                lookupsDefs.push({'name' : Object.keys(lookups.inverseValuesLookup[i])[0], 'lookupType' : 'inverseValuesLookup'});
            }
            // TODO: get from server or cache
            return lookupsDefs;
        };
        
        var lookups = {
            "standardLookup" : [{
                  "addressTypes" : [{
                      "key" : "Business",
                      "value" : "some.translation.key"
                    }, {
                      "key" : "Home",
                      "value" : "some.other.translation.key"
                    }
                  ]
             }, {
             "aliasTypes" : [{
                  "key" : "FKA",
                  "value" : "fka.translation.key"
                }, {
                  "key" : "maried",
                  "value" : "maried.translation.key"
                }
              ]
            }
            ],
            "subLookup" : [{
              "contactMethodTypes" : [{
                  "key" : "phone",
                  "value" : "phone.translation.key",
                  "subLookup" : [{
                      "key" : "Home",
                      "value" : "Home.translation.key"
                    }, {
                      "key" : "Work",
                      "value" : "Work.translation.key"
                    }, {
                      "key" : "Mobile",
                      "value" : "Mobile"
                    }
                  ]
                }, {
                  "key" : "fax",
                  "value" : "fax",
                  "subLookup" : [{
                      "key" : "subFax",
                      "value" : "subFax"
                    }
                  ]
                }
              ]
            }
            ],
            "inverseValuesLookup" : [{
              "organizationRelationTypes" : [{
                  "key" : "Partner",
                  "value" : "Partner.translation.key",
                  "inverseKey" : "Partner",
                  "inverseValue" : "Partner.translation.key"
                }, {
                  "key" : "SubCompany",
                  "value" : "SubCompany.translation.key",
                  "inverseKey" : "ParentCompany",
                  "inverseValue" : "ParentCompany.translation.key"
                }
              ]
            }
            ]
        }
        
        Service.getLookup = function (lookupDef) {
            // TODO: get from server or cache
            for (var i = 0, len = lookups[lookupDef.lookupType].length; i < len; i++) {
                if (lookups[lookupDef.lookupType][i].hasOwnProperty(lookupDef.name)) {
                    // return a deep copy of the lookup not to allow clients to change the original object
                    return _.cloneDeep(lookups[lookupDef.lookupType][i][lookupDef.name]);
                }
            }
        };
        
        Service.validateLookup = function (lookupDef, lookup) {
            switch(lookupDef.lookupType) {
                case 'standardLookup' :                    
                    return validateStandardLookup(lookup);
                case 'subLookup' :
                    return validateSubLookup(lookup);
                case 'inverseValuesLookup' :
                    return validateInverseValuesLookup(lookup);
                default:
                    console.error("Unknown lookup type!");
                    return { isValid : false, errorMessage: "Unknown lookup type!" };
            }
        };
        
        function validateStandardLookup(lookup) {
            // Check empty key or value
            for (var i = 0, len = lookup.length; i < len; i++) {
                if (!lookup[i].key) {
                    return { isValid : false, errorMessage: "Empty key found!" };
                }
                if (!lookup[i].value) {
                    return { isValid : false, errorMessage: "Empty value found!" };
                }
            }
            
            // Check duplicate keys or values
            for (var i = 0, len = lookup.length; i < len; i++) {
                for (var j = i + 1; j < len; j++) {
                    if (lookup[i].key === lookup[j].key) {
                        return { isValid : false, errorMessage: "Duplicate key found! [key : " +  lookup[i].key + "]" };
                    }
                    if (lookup[i].value === lookup[j].value) {
                        return { isValid : false, errorMessage: "Duplicate value found! [value : " +  lookup[i].value + "]" };
                    }
                }
            }
            
            return { isValid : true };
        };
        
        function validateSubLookup(lookup) {
            // Check empty keys or values
            for (var i = 0, len = lookup.length; i < len; i++) {
                if (!lookup[i].key) {
                    return { isValid : false, errorMessage: "Empty key found!" };
                }
                if (!lookup[i].value) {
                    return { isValid : false, errorMessage: "Empty value found!" };
                }
                // check sublookup for empty keys or values
                if (lookup[i].subLookup) {
                    for (var j = 0, lenSub = lookup[i].subLookup.length; j < lenSub; j++) {
                        if (!lookup[i].subLookup[j].key) {
                            return { isValid : false, errorMessage: "Empty key found!" };
                        }
                        if (!lookup[i].subLookup[j].value) {
                            return { isValid : false, errorMessage: "Empty value found!" };
                        }
                    }
                }
            }
            
            // Check duplicate keys or values
            for (var i = 0, len = lookup.length; i < len; i++) {
                for (var j = i + 1; j < len; j++) {
                    if (lookup[i].key === lookup[j].key) {
                        return { isValid : false, errorMessage: "Duplicate key found! [key : " +  lookup[i].key + "]" };
                    }
                    if (lookup[i].value === lookup[j].value) {
                        return { isValid : false, errorMessage: "Duplicate value found! [value : " +  lookup[i].value + "]" };
                    }
                    // check sublookup for duplicate keys or values
                    if (lookup[i].subLookup) {
                        for (var k = 0, lenSub = lookup[i].subLookup.length; k < lenSub; k++) {
                            for (var l = k + 1; l < lenSub; l++) {
                                if (lookup[i].subLookup[k].key === lookup[i].subLookup[l].key) {
                                    return { isValid : false, errorMessage: "Duplicate key found! [key : " +  lookup[i].subLookup[k].key + "]" };
                                }
                                if (lookup[i].subLookup[k].value === lookup[i].subLookup[l].value) {
                                    return { isValid : false, errorMessage: "Duplicate value found! [value : " +  lookup[i].subLookup[k].value + "]" };
                                }
                            }
                        }
                    }
                }
            }
            
            return { isValid : true };
        };
        
        function validateInverseValuesLookup(lookup) {
            // Check empty key or value
            for (var i = 0, len = lookup.length; i < len; i++) {
                if (!lookup[i].key) {
                    return { isValid : false, errorMessage: "Empty key found!" };
                }
                if (!lookup[i].value) {
                    return { isValid : false, errorMessage: "Empty value found!" };
                }
                if (!lookup[i].inverseKey) {
                    return { isValid : false, errorMessage: "Empty inverse key found!" };
                }
                if (!lookup[i].inverseValue) {
                    return { isValid : false, errorMessage: "Empty inverse value found!" };
                }
            }
            
            // Check duplicate keys or values
            for (var i = 0, len = lookup.length; i < len; i++) {
                for (var j = i + 1; j < len; j++) {
                    if (lookup[i].key === lookup[j].key) {
                        return { isValid : false, errorMessage: "Duplicate key found! [key : " +  lookup[i].key + "]" };
                    }
                    if (lookup[i].inverseKey === lookup[j].inverseKey) {
                        return { isValid : false, errorMessage: "Duplicate inverse key found! [key : " +  lookup[i].inverseKey + "]" };
                    }
                    if (lookup[i].value === lookup[j].value) {
                        return { isValid : false, errorMessage: "Duplicate value found! [key : " +  lookup[i].value + "]" };
                    }
                    if (lookup[i].inverseValue === lookup[j].inverseValue) {
                        return { isValid : false, errorMessage: "Duplicate inverse value found! [key : " +  lookup[i].inverseValue + "]" };
                    }
                }
            }
            
            return { isValid : true };
        };

        Service.saveLookup = function (lookupDef, lookup) {
            var validationResult = Service.validateLookup(lookupDef, lookup);
            if (!validationResult.isValid) {
                return Util.errorPromise(validationResult.errorMessage);
            }
            
            // save a deep copy of the lookup not to allow clients to change the object
            var lookupTosave = Util.omitNg(lookup);
            // TODO: save on server
            var lookupUpdated = false;
            for (var i = 0, len = lookups[lookupDef.lookupType].length; i < len; i++) {
                if (lookups[lookupDef.lookupType][i].hasOwnProperty(lookupDef.name)) {
                    lookups[lookupDef.lookupType][i][lookupDef.name] = lookupTosave;
                    lookupUpdated = true;
                    break;
                }
            }
            if (!lookupUpdated) {
                lookups[lookupDef.lookupType][lookupDef.name] = lookupTosave;
            }
            
            var deferred = $q.defer()
            deferred.resolve("Successfully resolved the fake $http call")
            return deferred.promise;
        };
        
        return Service;
    }
]);