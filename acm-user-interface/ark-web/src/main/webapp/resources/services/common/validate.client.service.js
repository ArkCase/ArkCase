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
            if (0 >= Util.goodValue(data.id), 0) {
                return false;
            }
            if (Util.isEmpty(data.caseNumber)) {
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

        , validateCorrespondences: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.containerObjectId)) {
                return false;
            }
            if (Util.isEmpty(data.folderId)) {
                return false;
            }
            if (Util.isEmpty(data.totalChildren)) {
                return false;
            }
            if (!Util.isArray(data.children)) {
                return false;
            }
            for (var i = 0; i < data.children.length; i++) {
                if (!this.validateCorrespondence(data.children[i])) {
                    return false;
                }
            }
            if ("Correspondence" != data.category) {
                return false;
            }
            return true;
        }
        , validateCorrespondence: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.objectId)) {
                return false;
            }
            if (Util.isEmpty(data.name)) {
                return false;
            }
            if (Util.isEmpty(data.created)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if ("file" != data.objectType) {
                return false;
            }
            if ("Correspondence" != data.category) {
                return false;
            }
            return true;
        }
        , validateNewCorrespondence: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.category)) {
                return false;
            }
            if ("Correspondence" != data.category) {
                return false;
            }
            if (Util.isEmpty(data.created)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            if (Util.isEmpty(data.fileName)) {
                return false;
            }
            if (Util.isEmpty(data.fileType)) {
                return false;
            }
            return true;
        }


        , validateCostsheets: function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateCostsheet(data[i])) {
                    return false;
                }
            }
            return true;
        }
        , validateCostsheet: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.user)) {
                return false;
            }
            if (Util.isEmpty(data.user.userId)) {
                return false;
            }
            if (Util.isEmpty(data.parentId)) {
                return false;
            }
            if (Util.isEmpty(data.parentType)) {
                return false;
            }
            if (Util.isEmpty(data.parentNumber)) {
                return false;
            }
            if (!Util.isArray(data.costs)) {
                return false;
            }
            if (Util.isEmpty(data.status)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            return true;
        }
        , validateCostRecord: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.title)) {
                return false;
            }
            if (Util.isEmpty(data.value)) {
                return false;
            }
            if (Util.isEmpty(data.date)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if (Util.isEmpty(data.modified)) {
                return false;
            }
            return true;
        }


        , validateTimesheets: function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateTimesheet(data[i])) {
                    return false;
                }
            }
            return true;
        }
        , validateTimesheet: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.user)) {
                return false;
            }
            if (Util.isEmpty(data.user.userId)) {
                return false;
            }
            if (Util.isEmpty(data.startDate)) {
                return false;
            }
            if (Util.isEmpty(data.endDate)) {
                return false;
            }
            if (!Util.isArray(data.times)) {
                return false;
            }
            if (Util.isEmpty(data.status)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if (Util.isEmpty(data.modified)) {
                return false;
            }

            return true;
        }
        , validateTimeRecord: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.code)) {
                return false;
            }
            if (Util.isEmpty(data.type)) {
                return false;
            }
            if (Util.isEmpty(data.objectId)) {
                return false;
            }
            if (Util.isEmpty(data.value)) {
                return false;
            }
            if (Util.isEmpty(data.creator)) {
                return false;
            }
            if (Util.isEmpty(data.modified)) {
                return false;
            }
            if (Util.isEmpty(data.date)) {
                return false;
            }
            return true;
        }        
    }}
]);