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

angular.module('services').factory('Object.LookupService', [ '$q', '$resource', 'Acm.StoreService', 'UtilService', 'LookupService', 'SearchService', function($q, $resource, Store, Util, LookupService, SearchService) {

    var Service = $resource('api/latest/plugin', {}, {
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
        _getGroups: {
            url: "api/latest/service/functionalaccess/groups/acm-complaint-approve?n=1000&s=name asc",
            method: "GET",
            cache: false
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
        ,
        _getFormTypes: {
            url: "api/latest/plugin/admin/plainforms/:objType",
            method: "GET",
            cache: false,
            isArray: true
        }
    });

    Service.SessionCacheNames = {
        PRIORITIES: "AcmPriorities",
        OWNING_GROUPS: "AcmOwningGroups",
        FORM_TYPE_MAP: "AcmFormTypeMap",
        PERSON_TYPES: "AcmPersonTypes"
    };
    Service.CacheNames = {};

    Service.getLookupTypes = [ 'standardLookup', 'inverseValuesLookup', 'nestedLookup' ];

    /**
     * @ngdoc method
     * @name getAuditReportNames
     * @methodOf services:Object.LookupService
     *
     * @description
     * Returns a list of audit reports
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getAuditReportNames = function() {
        return Service.getLookupByLookupName("auditReportNames");
    };

    Service.getResetRepeatPeriod = function() {
        return Service.getLookupByLookupName("sequenceResetPeriod");
    };

    /**
     * @ngdoc method
     * @name getSequenceName
     * @methodOf services:Object.LookupService
     *
     * @description
     * Returns a list of sequence names
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getSequenceName = function() {
        return Service.getLookupByLookupName("sequenceName");
    };
    
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
    Service.getPriorities = function() {
        return Service.getLookupByLookupName("priorities");
    };

    /**
     * @ngdoc method
     * @name getPriorities
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of sequenceManagementParts
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getSequenceParts = function () {
        return Service.getLookupByLookupName("sequenceParts");
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
    Service.validatePriorities = function(data) {
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
    Service.getGroups = function() {
        var cacheGroups = new Store.SessionData(Service.SessionCacheNames.OWNING_GROUPS);
        var groups = cacheGroups.get();
        return Util.serviceCall({
            service: Service._getGroups,
            result: groups,
            onSuccess: function(data) {
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
    Service.validateGroups = function(data) {
        if (!Util.validateSolrData(data)) {
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
    Service.getFormTypes = function(objType) {
        var cacheFormTypeMap = new Store.SessionData(Service.SessionCacheNames.FORM_TYPE_MAP);
        var formTypeMap = cacheFormTypeMap.get();
        var formTypes = Util.goodMapValue(formTypeMap, objType, null);

        return Util.serviceCall({
            service: Service._getFormTypes,
            param: {
                objType: objType
            },
            result: formTypes,
            onSuccess: function(data) {
                if (Service.validatePlainForms(data)) {
                    var plainForms = data;
                    formTypes = [];
                    _.each(plainForms, function(plainForm) {
                        var formType = {};
                        formType.key = plainForm.key;
                        formType.value = Util.goodValue(plainForm.name);
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
    Service.validatePlainForms = function(data) {
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
    Service.validatePlainForm = function(data) {
        if (Util.isEmpty(data)) {
            return false;
        }
        if (Util.isEmpty(data.key) && Util.isEmpty(data.type)) { //different attribute name. service data use "key"; menu item use "type"
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
    Service.getPersonTypes = function(objectType, initiator) {
        switch (objectType) {
        case "COMPLAINT":
            if (initiator) {
                return Service.getLookupByLookupName("complaintPersonInitiatorTypes");
            } else {
                return Service.getLookupByLookupName("complaintPersonTypes");
            }
        case "CASE_FILE":
            if (initiator) {
                return Service.getLookupByLookupName("caseFilePersonInitiatorTypes");
            } else {
                return Service.getLookupByLookupName("caseFilePersonTypes");
            }
        case "DOC_REPO":
            return Service.getLookupByLookupName("documentPersonTypes");
        }
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
    Service.validatePersonTypes = function(data) {
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
    Service.getFileTypes = function() {
        return Service.getLookupByLookupName("fileTypes");
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
    Service.getParticipantTypes = function(objectType) {
        switch (objectType) {
        case "PERSON":
        case "ORGANIZATION":
            return Service.getLookupByLookupName("organizationalParticipantTypes");
        case "COMPLAINT":
        case "CASE_FILE":
        case "DOC_REPO":
            return Service.getLookupByLookupName("entitiesParticipantTypes");
        case "FILE":
        case "FOLDER":
            return Service.getLookupByLookupName("documentsParticipantTypes");
        }
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
    Service.getPersonTitles = function() {
        return Service.getLookupByLookupName("personTitles");
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
    Service.getContactMethodTypes = function() {
        return Service.getLookupByLookupName("contactMethodTypes");
    };

    Service.getSubContactMethodType = function(type) {
        return Service.getLookupByLookupName("contactMethodTypes").then(function(contactMethodTypes) {
            var found = _.find(contactMethodTypes, {
                key: type
            });
            if (!Util.isArray(found)) {
                return found.subLookup;
            }
        });
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
    Service.getOrganizationTypes = function() {
        return Service.getLookupByLookupName("organizationTypes");
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
    Service.getAddressTypes = function() {
        return Service.getLookupByLookupName("addressTypes");
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
    Service.getAliasTypes = function() {
        return Service.getLookupByLookupName("aliasTypes");
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
    Service.getPersonRelationTypes = function() {
        return Service.getLookupByLookupName("personRelationTypes");
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
    Service.getOrganizationRelationTypes = function() {
        return Service.getLookupByLookupName("organizationRelationTypes");
    };

    /**
     * @ngdoc method
     * @name getCaseFileTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of case file types
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getCaseFileTypes = function() {
        return Service.getLookupByLookupName("caseFileTypes");
    };

    /**
     * @ngdoc method
     * @name getComplaintTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of complaint types
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getComplaintTypes = function() {
        return Service.getLookupByLookupName("complaintTypes");
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
    Service.getObjectTypes = function() {
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
    Service.getCaseFileCorrespondenceForms = function() {
        var df = $q.defer();
        var forms = LookupService.getLookup("caseCorrespondenceForms");
        forms.then(function(forms) {
            var activated = _.filter(forms, function(form) {
                return form.activated == true;
            });
            df.resolve(activated);
        }, function(err) {
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
    Service.getComplaintCorrespondenceForms = function() {
        var df = $q.defer();
        var forms = LookupService.getLookup("complaintCorrespondenceForms");
        forms.then(function(forms) {
            var activated = _.filter(forms, function(form) {
                return form.activated == true;
            });
            df.resolve(activated);
        }, function(err) {
            df.reject(err);
        });
        return df.promise;
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
    Service.getIdentificationTypes = function() {
        return Service.getLookupByLookupName("identificationTypes");
    };

    /**
     * @ngdoc method
     * @name getCmisVersioningState
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of identification types
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getCmisVersioningState = function() {
        return Service.getLookupByLookupName("cmisVersioningState");
    };

    /**
     * @ngdoc method
     * @name getCorrespondenceObjectTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of correspondence object types
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getCorrespondenceObjectTypes = function() {
        return Service.getLookupByLookupName("correspondenceObjectTypes");
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
    Service.getOrganizationIdTypes = function() {
        return Service.getLookupByLookupName("organizationIdTypes");
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
    Service.getDBAsTypes = function() {
        return Service.getLookupByLookupName("dbasTypes");
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
    Service.getCountries = function() {
        return Service.getLookupByLookupName("countries");
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
    Service.getPersonOrganizationRelationTypes = function() {
        return Service.getLookupByLookupName("personOrganizationRelationTypes");
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
    Service.getOrganizationPersonRelationTypes = function() {
        return Service.getLookupByLookupName("organizationPersonRelationTypes");
    };

    /**
     * @ngdoc method
     * @name getBusinessProcessTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of Business Process Types
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getBusinessProcessTypes = function() {
        return Service.getLookupByLookupName('businessProcessTypes');
    };

     /**
     * @ngdoc method
     * @name getCostsheetTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of Costsheet Types
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getCostsheetTypes = function() {
        return Service.getLookupByLookupName("costsheetTypes");
    };

    /**
     * @ngdoc method
     * @name getTimesheetTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of Timesheet Types
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getTimesheetTypes = function() {
        return Service.getLookupByLookupName("timesheetTypes");
    };

    /**
     * @ngdoc method
     * @name getCostsheetTitles
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of Costsheet Titles
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getCostsheetTitles = function() {
        return Service.getLookupByLookupName("costsheetTitles");
    };

    /**
     * @ngdoc method
     * @name getCostsheetStatuses
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of Costsheet Statuses
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getCostsheetStatuses = function() {
        return Service.getLookupByLookupName("costsheetStatuses");
    };

    /**
     * @ngdoc method
     * @name getTimesheetStatuses
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of Timesheet Statuses
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getTimesheetStatuses = function() {
        return Service.getLookupByLookupName("timesheetStatuses");
    };

    /**
     * @ngdoc method
     * @name getTimesheetChargeRoles
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of Timesheet Statuses
     *
     * @returns {Object} An array returned by $resource
     */
    Service.getTimesheetChargeRoles = function() {
        return Service.getLookupByLookupName("timesheetChargeRoles");
    };

    /**
     * @ngdoc method
     * @name getDispositionTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of dispositionTypes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getDispositionTypes = function(objectType) {
        switch (objectType) {
        case "New Request":
            return Service.getLookupByLookupName('requestDispositionType');
        case "Appeal":
            return Service.getLookupByLookupName('appealDispositionType');
        }
    };

    /**
     * @ngdoc method
     * @name getRequestDispositionSubTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all requestDispositionSubType
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getRequestDispositionSubTypes = function() {
        return Service.getLookupByLookupName('requestDispositionSubType');
    };

    /**
     * @ngdoc method
     * @name getAppealDispositionSubTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all appealDispositionSubType
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getAppealDispositionSubTypes = function() {
        return Service.getLookupByLookupName('appealDispositionSubType');

    };

    /**
     * @ngdoc method
     * @name getRequestTrack
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all requestTrack
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getRequestTrack = function() {
        return Service.getLookupByLookupName('requestTrack');

    };

    /**
     * @ngdoc method
     * @name getExemptionStatutes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all exemptionStatutes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getExemptionStatutes = function() {
        return Service.getLookupByLookupName('exemptionStatutes');

    };

    /**
     * @ngdoc method
     * @name getPrefixes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all exemptionStatutes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getPrefixes = function() {
        return Service.getLookupByLookupName('prefixNewRequest');

    };

    /**
     * @ngdoc method
     * @name getRequestTypes
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all exemptionStatutes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getRequestTypes = function() {
        return Service.getLookupByLookupName('requestTypes');

    };

    /**
     * @ngdoc method
     * @name getStates
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all exemptionStatutes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getStates = function() {
        return Service.getLookupByLookupName('states');

    };

    /**
     * @ngdoc method
     * @name getRequestCategories
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all exemptionStatutes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getRequestCategories = function() {
        return Service.getLookupByLookupName('requestCategory');

    };

    /**
     * @ngdoc method
     * @name getDeliveryMethodOfResponses
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all exemptionStatutes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getDeliveryMethodOfResponses = function() {
        return Service.getLookupByLookupName('deliveryMethodOfResponses');

    };

    /**
     * @ngdoc method
     * @name getPayFees
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all exemptionStatutes
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getPayFees = function() {
        return Service.getLookupByLookupName('payFees');

    };


    /**
     * @ngdoc method
     * @name getAnnotationTags
     * @methodOf services:Object.LookupService
     *
     * @description
     * Query list of all annotationTags
     *
     * @returns {Object} An array returned by $resource
     */

    Service.getAnnotationTags = function() {
        return Service.getLookupByLookupName('annotationTags');

    };

    /**
     * @ngdoc method
     * @name getLookupsDefs
     * @methodOf services:Object.LookupService
     *
     * @description
     * Returns all lookup definitions as array with structure:
     * [
     *      { 'lookupType' : 'standardLookup', 'name' : 'addressTypes' },
     *      { 'lookupType' : 'standardLookup', 'name' : 'aliasTypes' },
     *      { 'lookupType' : 'nestedLookup', 'name' : 'contactMethodTypes' },
     *      { 'lookupType' : 'inverseValuesLookup', 'name' : 'organizationPersonRelationTypes' },
     *      { ... }
     * ]
     *
     * @returns {Object} Promise returning all lookup definitions in an array
     */
    Service.getLookupsDefs = function() {
        return LookupService.getLookups().then(function(lookups) {
            var lookupsDefs = [];
            if (lookups.standardLookup) {
                for (var i = 0, len = lookups.standardLookup.length; i < len; i++) {
                    lookupsDefs.push({
                        'name': lookups.standardLookup[i].name,
                        'lookupType': 'standardLookup',
                        'readonly': lookups.standardLookup[i].readonly
                    });
                }
            }
            if (lookups.nestedLookup) {
                for (var i = 0, len = lookups.nestedLookup.length; i < len; i++) {
                    lookupsDefs.push({
                        'name': lookups.nestedLookup[i].name,
                        'lookupType': 'nestedLookup',
                        'readonly': lookups.nestedLookup[i].readonly
                    });
                }
            }
            if (lookups.inverseValuesLookup) {
                for (var i = 0, len = lookups.inverseValuesLookup.length; i < len; i++) {
                    lookupsDefs.push({
                        'name': lookups.inverseValuesLookup[i].name,
                        'lookupType': 'inverseValuesLookup',
                        'readonly': lookups.inverseValuesLookup[i].readonly
                    });
                }
            }

            return lookupsDefs;
        });

    };

    /**
     * @ngdoc method
     * @name getLookup
     * @methodOf services:Object.LookupService
     *
     * @description
     * Returns lookup entries as array.
     *
     * @param {Object} lookupDef    The lookup definition with structure:
     *                              { 'lookupType' : 'standardLookup', 'name' : 'addressTypes' }
     *
     * @returns {Object} Promise returning the lookup entries as array
     */
    Service.getLookup = function(lookupDef) {
        return Service.getLookupByLookupName(lookupDef.name);
    };

    /**
     * @ngdoc method
     * @name getLookupByLookupName
     * @methodOf services:Object.LookupService
     *
     * @description
     * Returns lookup entries as array.
     *
     * @param {String} name    The lookup name
     *
     * @returns {Object} Promise returning the lookup entries as array
     */
    Service.getLookupByLookupName = function(name) {
        return LookupService.getLookups().then(function(lookups) {
            for ( var lookupType in lookups) {
                if (lookups.hasOwnProperty(lookupType)) {
                    for (var i = 0, len = lookups[lookupType].length; i < len; i++) {
                        if (lookups[lookupType][i].name == name) {
                            // return a deep copy of the lookup not to allow clients to change the original object
                            return _.cloneDeep(lookups[lookupType][i].entries);
                        }
                    }
                }
            }
        });
    };

    /**
     * @ngdoc method
     * @name validateLookup
     * @methodOf services:Object.LookupService
     *
     * @description
     * Validate lookup data
     *
     * @param {Object} lookupDef    the lookup definition to be validated with structure:
     *                              { 'lookupType' : 'standardLookup', 'name' : 'addressTypes' }
     * @parma {Array}  lookup       the lookup entries as an array.
     *                              For standarLookup valid structure looks like:
     *                              [{'key':'1', 'value':'1'}, {'key':'2', 'value':'2'}, {...}]
     *                              For nestedLookup valid structure looks like:
     *                              [{'key':'1', 'value':'1', 'subLookup' : [{'key':'11', 'value':'11'}, {'key':'12', 'value':'12'}]}, {...}]
     *                              For inverseValuesLookup valid structure looks like:
     *                              [{'key':'1', 'value':'1', 'inverseKey':'inv1', 'inverseValue':'inv1'}, {'key':'2', 'value':'2', 'inverseKey':'inv2', 'inverseValue':'inv2'}]
     *
     * @returns {Object} returns data validation object with structure:
     *                              { isValid : false, errorMessage: "The validation error message" }
     *                              or
     *                              { isValid : true }
     */
    Service.validateLookup = function(lookupDef, lookup) {
        switch (lookupDef.lookupType) {
        case 'standardLookup':
            return validateStandardLookup(lookup);
        case 'nestedLookup':
            return validateNestedLookup(lookup);
        case 'inverseValuesLookup':
            return validateInverseValuesLookup(lookup);
        default:
            console.error("Unknown lookup type!");
            return {
                isValid: false,
                errorMessage: "Unknown lookup type!"
            };
        }
    };

    function validateStandardLookup(lookup) {
        // Check empty key or value
        for (var i = 0, len = lookup.length; i < len; i++) {
            if (!lookup[i].value) {
                return {
                    isValid: false,
                    errorMessage: "Empty value found!"
                };
            }
        }

        // Check duplicate keys or values
        for (var i = 0, len = lookup.length; i < len; i++) {
            for (var j = i + 1; j < len; j++) {
                if (lookup[i].key === lookup[j].key) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate key found! [key : " + lookup[i].key + "]"
                    };
                }
                if (lookup[i].value === lookup[j].value) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate value found! [value : " + lookup[i].value + "]"
                    };
                }
            }
        }

        return {
            isValid: true
        };
    }
    ;

    function validateNestedLookup(lookup) {
        // Check empty keys or values
        for (var i = 0, len = lookup.length; i < len; i++) {
            if (!lookup[i].value) {
                return {
                    isValid: false,
                    errorMessage: "Empty value found!"
                };
            }
            // check sublookup for empty keys or values
            if (lookup[i].subLookup) {
                for (var j = 0, lenSub = lookup[i].subLookup.length; j < lenSub; j++) {
                    if (!lookup[i].subLookup[j].value) {
                        return {
                            isValid: false,
                            errorMessage: "Empty value found!"
                        };
                    }
                }
            }
        }

        // Check duplicate keys or values
        for (var i = 0, len = lookup.length; i < len; i++) {
            for (var j = i + 1; j < len; j++) {
                if (lookup[i].key === lookup[j].key) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate key found! [key : " + lookup[i].key + "]"
                    };
                }
                if (lookup[i].value === lookup[j].value) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate value found! [value : " + lookup[i].value + "]"
                    };
                }
                // check sublookup for duplicate keys or values
                if (lookup[i].subLookup) {
                    for (var k = 0, lenSub = lookup[i].subLookup.length; k < lenSub; k++) {
                        for (var l = k + 1; l < lenSub; l++) {
                            if (lookup[i].subLookup[k].key === lookup[i].subLookup[l].key) {
                                return {
                                    isValid: false,
                                    errorMessage: "Duplicate key found! [key : " + lookup[i].subLookup[k].key + "]"
                                };
                            }
                            if (lookup[i].subLookup[k].value === lookup[i].subLookup[l].value) {
                                return {
                                    isValid: false,
                                    errorMessage: "Duplicate value found! [value : " + lookup[i].subLookup[k].value + "]"
                                };
                            }
                        }
                    }
                }
            }
        }

        return {
            isValid: true
        };
    }
    ;

    function validateInverseValuesLookup(lookup) {
        // Check empty key or value
        for (var i = 0, len = lookup.length; i < len; i++) {
            if (!lookup[i].value) {
                return {
                    isValid: false,
                    errorMessage: "Empty value found!"
                };
            }
            if (!lookup[i].inverseValue) {
                return {
                    isValid: false,
                    errorMessage: "Empty inverse value found!"
                };
            }
        }

        // Check duplicate keys or values
        for (var i = 0, len = lookup.length; i < len; i++) {
            for (var j = i + 1; j < len; j++) {
                if (lookup[i].key === lookup[j].key) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate key found! [key : " + lookup[i].key + "]"
                    };
                }
                if (lookup[i].inverseKey === lookup[j].inverseKey) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate inverse key found! [key : " + lookup[i].inverseKey + "]"
                    };
                }
                if (lookup[i].value === lookup[j].value) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate value found! [key : " + lookup[i].value + "]"
                    };
                }
                if (lookup[i].inverseValue === lookup[j].inverseValue) {
                    return {
                        isValid: false,
                        errorMessage: "Duplicate inverse value found! [key : " + lookup[i].inverseValue + "]"
                    };
                }
            }
        }

        return {
            isValid: true
        };
    }
    ;

    /**
     * @ngdoc method
     * @name saveLookup
     * @methodOf services:Object.LookupService
     *
     * @description
     * Saves the the given lookup entries for the lookup definition.
     * First makes a deep copy of the lookup to prevent modification by clients.
     *
     * @param {Object} lookupDef    the lookup definition to be saved with structure:
     *                              { 'lookupType' : 'standardLookup', 'name' : 'addressTypes' }
     * @parma {Array}  lookup       the lookup entries as an array.
     *                              For standarLookup the structure looks like:
     *                              [{'key':'1', 'value':'1'}, {'key':'2', 'value':'2'}, {...}]
     *                              For nestedLookup the structure looks like:
     *                              [{'key':'1', 'value':'1', 'subLookup' : [{'key':'11', 'value':'11'}, {'key':'12', 'value':'12'}]}, {...}]
     *                              For inverseValuesLookup the structure looks like:
     *                              [{'key':'1', 'value':'1', 'inverseKey':'inv1', 'inverseValue':'inv1'}, {'key':'2', 'value':'2', 'inverseKey':'inv2', 'inverseValue':'inv2'}]
     *
     * @returns {Object} Promise returning all lookups from server.
     */
    Service.saveLookup = function(lookupDef, lookup) {
        // save a deep copy of the lookup not to allow clients to change the object
        var lookupTosave = Util.omitNg(lookup);

        var validationResult = Service.validateLookup(lookupDef, lookupTosave);
        if (!validationResult.isValid) {
            return Util.errorPromise(validationResult.errorMessage);
        }

        return LookupService.saveLookup(lookupDef, lookupTosave);
    };

    return Service;
} ]);
