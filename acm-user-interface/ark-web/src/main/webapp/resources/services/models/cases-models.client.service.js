'use strict';

// Authentication service for user variables
angular.module('services').factory('CasesModelsService', ['$q', '$resource', 'CasesService', 'ConfigService', 'ValidationService', 'UtilService',
    function ($q, $resource, CasesService, ConfigService, Validator, Util) {
        return {


            //
            // These help functions are common for case, complaint, task. Move to ObjectsModelsService
            //


            //, getAssignee: function(caseFile) {
            //    var assignee = null;
            //    if (Validator.validateCaseFile(caseFile)) {
            //        if (Util.isArray(caseFile.participants)) {
            //            for (var i = 0; i < caseFile.participants.length; i++) {
            //                var participant =  caseFile.participants[i];
            //                if ("assignee" == participant.participantType) {
            //                    assignee = participant.participantLdapId;
            //                    break;
            //                }
            //            }
            //        }
            //    }
            //    return assignee;
            //}
            //, setAssignee: function(caseFile, assignee) {
            //    if (caseFile) {
            //        if (!Util.isArray(caseFile.participants)) {
            //            caseFile.participants = [];
            //        }
            //
            //        for (var i = 0; i < caseFile.participants.length; i++) {
            //            if ("assignee" == caseFile.participants[i].participantType) {
            //                caseFile.participants[i].participantLdapId = assignee;
            //                return;
            //            }
            //        }
            //
            //        var participant = {};
            //        participant.participantType = "assignee";
            //        participant.participantLdapId = assignee;
            //        caseFile.participants.push(participant);
            //    }
            //}
            //, getGroup: function(caseFile) {
            //    var group = null;
            //    if (Validator.validateCaseFile(caseFile)) {
            //        if (Util.isArray(caseFile.participants)) {
            //            for (var i = 0; i < caseFile.participants.length; i++) {
            //                var participant =  caseFile.participants[i];
            //                if ("owning group" == participant.participantType) {
            //                    group = participant.participantLdapId;
            //                    break;
            //                }
            //            }
            //        }
            //    }
            //    return group;
            //}
            //, setGroup: function(caseFile, group) {
            //    if (caseFile) {
            //        if (!Util.isArray(caseFile.participants)) {
            //            caseFile.participants = [];
            //        }
            //
            //        for (var i = 0; i < caseFile.participants.length; i++) {
            //            if ("owning group" == caseFile.participants[i].participantType) {
            //                caseFile.participants[i].participantLdapId = group;
            //                return;
            //            }
            //        }
            //
            //        var participant = {};
            //        participant.participantType = "owning group";
            //        participant.participantLdapId = group;
            //        caseFile.participants.push(participant);
            //    }
            //}
        }
    }
]);