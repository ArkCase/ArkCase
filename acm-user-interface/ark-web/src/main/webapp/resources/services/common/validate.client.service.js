'use strict';

/**
 * @ngdoc service
 * @name services:ValidationService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/common/validate.client.service.js services/common/validate.client.service.js}
 *
 * All external data (data from database, SOLR queries, configuration, etc.) need to be validated to ensure
 * the robustness of the web application. ValidationService contains validation functions used through out the
 * application. Each validation function has the same pattern: returns false for invalid data conditions one
 * by one and return true at the end to indicate data is validated.
 */
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
        ,validateUserInfo: function(data){
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.userId)) {
                return false;
            }
            if (Util.isEmpty(data.fullName)) {
                return false;
            }
            if (Util.isEmpty(data.mail)) {
                return false;
            }
            if (Util.isEmpty(data.firstName)) {
                return false;
            }
            if (Util.isEmpty(data.lastName)) {
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

        , validateFolderList: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data.children)) {
                return false;
            }
            return true;
        }
        , validateCreateInfo: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (0 == data.id) {
                return false;
            }
            if (Util.isEmpty(data.parentFolderId)) {
                return false;
            }
            return true;
        }
        , validateDeletedFolder: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedFolderId)) {
                return false;
            }
            return true;
        }
        , validateDeletedFile: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.deletedFileId)) {
                return false;
            }
            return true;
        }
        , validateRenamedFolder: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            if (Util.isEmpty(data.name)) {
                return false;
            }
            return true;
        }
        , validateRenamedFile: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            if (Util.isEmpty(data.fileName)) {
                return false;
            }
            return true;
        }
        , validateMoveFileInfo: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            if (Util.isEmpty(data.folder)) {
                return false;
            }
            if (Util.isEmpty(data.folder.id)) {
                return false;
            }
            return true;
        }
        , validateCopyFileInfo: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.originalId)) {
                return false;
            }
            if (Util.isEmpty(data.newFile)) {
                return false;
            }
            if (Util.isEmpty(data.newFile.fileId)) {
                return false;
            }
            if (Util.isEmpty(data.newFile.folder)) {
                return false;
            }
            if (Util.isEmpty(data.newFile.folder.id)) {
                return false;
            }
            return true;
        }
        , validateMoveFolderInfo: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.id)) {
                return false;
            }
            return true;
        }
        , validateCopyFolderInfo: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.originalFolderId)) {
                return false;
            }
            if (Util.isEmpty(data.newFolder)) {
                return false;
            }
            if (Util.isEmpty(data.newFolder.id)) {
                return false;
            }
            if (Util.isEmpty(data.newFolder.parentFolderId)) {
                return false;
            }
            return true;
        }
        , validateUploadInfo: function (data) {
            if (Util.isArrayEmpty(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateUploadInfoItem(data[i])) {
                    return false;
                }
            }
            return true;
        }
        , validateReplaceInfo: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            return true;
        }
        , validateUploadInfoItem: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            if (Util.isEmpty(data.folder)) {
                return false;
            }
            if (!Util.isArray(data.versions)) {
                return false;
            }
            if (!Util.isArray(data.tags)) {
                return false;
            }
            return true;
        }
        , validateActiveVersion: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (Util.isEmpty(data.fileId)) {
                return false;
            }
            if (Util.isEmpty(data.activeVersionTag)) {
                return false;
            }
            return true;
        }
        , validateSentEmails: function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validateSentEmail(data[i])) {
                    return false;
                }
            }
            return true;
        }
        , validateSentEmail: function (data) {
            if (Util.isEmpty(data.state)) {
                return false;
            }
            if (Util.isEmpty(data.userEmail)) {
                return false;
            }
            return true;
        }
        , validateFileTypes: function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            return true;
        }
        , validatePlainForms: function (data) {
            if (!Util.isArray(data)) {
                return false;
            }
            for (var i = 0; i < data.length; i++) {
                if (!this.validatePlainForm(data[i])) {
                    return false;
                }
            }
            return true;
        }
        , validatePlainForm: function (data) {
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
        }
        , validateConfigComponents: function (data) {
            if (Util.isEmpty(data)) {
                return false;
            }
            if (!Util.isArray(data.components)) {
                return false;
            }
            return true;
        }
    }}
]);