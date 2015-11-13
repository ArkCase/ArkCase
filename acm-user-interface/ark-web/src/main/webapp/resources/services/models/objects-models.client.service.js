'use strict';

/**
 * @ngdoc service
 * @name services.service:ObjectsModelsService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/models/objects-models.client.service.js services/models/objects-models.client.service.js}

 * CallObjectsService contains wrapper functions of ObjectsService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('ObjectsModelsService', ['$q', '$resource', 'UtilService',
    function ($q, $resource, Util) {
        return {

            /**
             * @ngdoc method
             * @name getAssignee
             * @methodOf services.service:ObjectsModelsService
             *
             * @description
             * Search for assignee from object data
             *
             * @param {Object} objectInfo  Object data. Can be CaseFile, Complaint, Task
             *
             * @returns {String} Assignee, or 'null' if not found
             */
            getAssignee: function (objectInfo) {
                var assignee = null;
                if (Util.isArray(objectInfo.participants)) {
                    for (var i = 0; i < objectInfo.participants.length; i++) {
                        var participant = objectInfo.participants[i];
                        if ("assignee" == participant.participantType) {
                            assignee = participant.participantLdapId;
                            break;
                        }
                    }
                }
                return assignee;
            }

            /**
             * @ngdoc method
             * @name setAssignee
             * @methodOf services.service:ObjectsModelsService
             *
             * @description
             * Set a assignee to an object data
             *
             * @param {Object} objectInfo  Object data. Can be CaseFile, Complaint, Task
             * @param {Object} assignee  Assignee LDAP ID
             *
             */
            , setAssignee: function (objectInfo, assignee) {
                if (Util.isArray(objectInfo.participants)) {
                    for (var i = 0; i < objectInfo.participants.length; i++) {
                        if ("assignee" == objectInfo.participants[i].participantType) {
                            objectInfo.participants[i].participantLdapId = assignee;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "assignee";
                    participant.participantLdapId = assignee;
                    objectInfo.participants.push(participant);
                }
            }

            /**
             * @ngdoc method
             * @name getGroup
             * @methodOf services.service:ObjectsModelsService
             *
             * @description
             * Search for group from object data
             *
             * @param {Object} objectInfo  Object data. Can be CaseFile, Complaint, Task
             *
             * @returns {String} Group, or 'null' if not found
             */
            , getGroup: function (objectInfo) {
                var group = null;
                if (Util.isArray(objectInfo.participants)) {
                    for (var i = 0; i < objectInfo.participants.length; i++) {
                        var participant = objectInfo.participants[i];
                        if ("owning group" == participant.participantType) {
                            group = participant.participantLdapId;
                            break;
                        }
                    }
                }
                return group;
            }

            /**
             * @ngdoc method
             * @name setGroup
             * @methodOf services.service:ObjectsModelsService
             *
             * @description
             * Set a group to an object data
             *
             * @param {Object} objectInfo  Object data. Can be CaseFile, Complaint, Task
             * @param {Object} group  Group LDAP ID
             *
             */
            , setGroup: function (objectInfo, group) {
                if (Util.isArray(objectInfo.participants)) {
                    for (var i = 0; i < objectInfo.participants.length; i++) {
                        if ("owning group" == objectInfo.participants[i].participantType) {
                            objectInfo.participants[i].participantLdapId = group;
                            return;
                        }
                    }

                    var participant = {};
                    participant.participantType = "owning group";
                    participant.participantLdapId = group;
                    objectInfo.participants.push(participant);
                }
            }
        }
    }
]);