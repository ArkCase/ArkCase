'use strict';

angular.module('services').factory('ValidationService', ["UtilService",
    function (Util) {return {

        validateUsers: function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        }
        ,validateUser: function(data){
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.name)) {
                return false;
            }
            if (Util.isEmpty(data.last_name_lcs)) {
                return false;
            }
            if (Util.isEmpty(data.first_name_lcs)) {
                return false;
            }
            if (Util.isEmpty(data.object_id_s)) {
                return false;
            }
            if (Util.isEmpty(data.object_type_s)) {
                return false;
            }
            return true;
        }
        , validateSolrData: function (data) {
            if (!data) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader) || Util.isEmpty(data.response)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.status)) {
                return false;
            }
//            if (0 != responseHeader.status) {
//                return false;
//            }
            if (Util.isEmpty(data.responseHeader.params)) {
                return false;
            }
            if (Util.isEmpty(data.responseHeader.params.q)) {
                return false;
            }

            if (Util.isEmpty(data.response.numFound) || Util.isEmpty(data.response.start)) {
                return false;
            }
            if (!Util.isArray(data.response.docs)) {
                return false;
            }
            return true;
        }
        ,validateCaseFile: function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id) || Util.isEmpty(data.caseNumber)) {
                return false;
            }
            if (!Util.isArray(data.childObjects)) {
                return false;
            }
            if (!Util.isArray(data.milestones)) {
                return false;
            }
            if (!Util.isArray(data.participants)) {
                return false;
            }
            if (!Util.isArray(data.personAssociations)) {
                return false;
            }
            if (!Util.isArray(data.references)) {
                return false;
            }
            return true;
        }
        ,validatePersonAssociations: function(data) {
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
        }
        ,validatePersonAssociation: function(data) {
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
        }
        ,validateDeletedPersonAssociation: function(data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedPersonAssociationId)) {
                return false;
            }
            return true;
        }
        , validateHistory: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data.resultPage)) {
                return false;
            }
            for (var i = 0; i < data.resultPage.length; i++) {
                if (!this.validateEvent(data.resultPage[i])) {
                    return false;
                }
            }
            if (Util.isEmpty(data.totalCount)) {
                return false;
            }
            return true;
        }
        , validateEvent: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.eventDate)) {
                return false;
            }
            if (Util.isEmpty(data.eventType)) {
                return false;
            }
            if (Util.isEmpty(data.objectId)) {
                return false;
            }
            if (Util.isEmpty(data.objectType)) {
                return false;
            }
            if (Util.isEmpty(data.userId)) {
                return false;
            }
            return true;
        }

        , validateNotes: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateNote(data[i])) {
                    return false;
                }
            }
            return true;
        }
        , validateNote: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.parentId)) {
                return false;
            }
            return true;
        }
        , validateDeletedNote: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedNoteId)) {
                return false;
            }
            return true;
        }
    }}
]);