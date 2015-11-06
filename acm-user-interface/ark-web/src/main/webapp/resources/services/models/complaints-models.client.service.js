'use strict';

// Authentication service for user variables
angular.module('services').factory('ComplaintsModelsService', ['$q', '$resource', 'ComplaintsService', 'ConfigService', 'ValidationService', 'UtilService',
    function ($q, $resource, ComplaintsService, ConfigService, Validator, Util) {
        return {
            getAssignee: function (complaint) {
                var assignee = null;
                if (Validator.validateComplaint(complaint)) {
                    if (Util.isArray(complaint.participants)) {
                        for (var i = 0; i < complaint.participants.length; i++) {
                            var participant = complaint.participants[i];
                            if ("assignee" == participant.participantType) {
                                assignee = participant.participantLdapId;
                                break;
                            }
                        }
                    }
                }
                return assignee;
            }
            , setAssignee: function (complaint, assignee) {
                if (complaint) {
                    if (!Util.isArray(complaint.participants)) {
                        complaint.participants = [];
                    }

                    for (var i = 0; i < complaint.participants.length; i++) {
                        if ("assignee" == complaint.participants[i].participantType) {
                            complaint.participants[i].participantLdapId = assignee;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "assignee";
                    participant.participantLdapId = assignee;
                    complaint.participants.push(participant);
                }
            }
            , getGroup: function (complaint) {
                var group = null;
                if (Validator.validateComplaint(complaint)) {
                    if (Util.isArray(complaint.participants)) {
                        for (var i = 0; i < complaint.participants.length; i++) {
                            var participant = complaint.participants[i];
                            if ("owning group" == participant.participantType) {
                                group = participant.participantLdapId;
                                break;
                            }
                        }
                    }
                }
                return group;
            }
            , setGroup: function (complaint, group) {
                if (complaint) {
                    if (!Util.isArray(complaint.participants)) {
                        complaint.participants = [];
                    }

                    for (var i = 0; i < complaint.participants.length; i++) {
                        if ("owning group" == complaint.participants[i].participantType) {
                            complaint.participants[i].participantLdapId = group;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "owning group";
                    participant.participantLdapId = group;
                    complaint.participants.push(participant);
                }
            }
        }
    }
]);