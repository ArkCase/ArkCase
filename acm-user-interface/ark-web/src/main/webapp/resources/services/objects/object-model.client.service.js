'use strict';

/**
 * @ngdoc service
 * @name services:Object.ModelService
 *
 * @description
 *
 * {@link https://github.com/Armedia/ACM3/blob/develop/acm-user-interface/ark-web/src/main/webapp/resources/services/object-model.client.service.js services/object-model.client.service.js}

 * CallObjectsService contains wrapper functions of ObjectsService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('Object.ModelService', ['$q', '$resource', 'UtilService',
    function ($q, $resource, Util) {
        return {

            /**
             * @ngdoc method
             * @name getAssignee
             * @methodOf services:Object.ModelService
             *
             * @description
             * Search for assignee from object data
             *
             * @param {Object} objectInfo  Object data. Can be CaseFile, Complaint, Task
             *
             * @returns {String} Assignee, or 'null' if not found
             */
            getAssignee: function (objectInfo) {
                return _.result(_.find(Util.goodMapValue(objectInfo, "participants", []), {participantType: "assignee"}), "participantLdapId");
                //above code is equivalent to following
                //var assignee = null;
                //if (Util.isArray(objectInfo.participants)) {
                //    for (var i = 0; i < objectInfo.participants.length; i++) {
                //        var participant = objectInfo.participants[i];
                //        if ("assignee" == participant.participantType) {
                //            assignee = participant.participantLdapId;
                //            break;
                //        }
                //    }
                //}
                //return assignee;
            }

            /**
             * @ngdoc method
             * @name setAssignee
             * @methodOf services:Object.ModelService
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
                    var found = _.find(objectInfo.participants, {participantType: "assignee"});
                    if (found) {
                        found.participantLdapId = assignee;
                    } else {
                        objectInfo.participants.push({
                            participantType: "assignee"
                            , participantLdapId: assignee
                        });
                    }
                    //for (var i = 0; i < objectInfo.participants.length; i++) {
                    //    if ("assignee" == objectInfo.participants[i].participantType) {
                    //        objectInfo.participants[i].participantLdapId = assignee;
                    //        return;
                    //    }
                    //}
                    //
                    //var participant = {};
                    //participant.participantType = "assignee";
                    //participant.participantLdapId = assignee;
                    //objectInfo.participants.push(participant);
                }
            }

            /**
             * @ngdoc method
             * @name getGroup
             * @methodOf services:Object.ModelService
             *
             * @description
             * Search for group from object data
             *
             * @param {Object} objectInfo  Object data. Can be CaseFile, Complaint, Task
             *
             * @returns {String} Group, or 'null' if not found
             */
            , getGroup: function (objectInfo) {
                return _.result(_.find(Util.goodMapValue(objectInfo, "participants", []), {participantType: "owning group"}), "participantLdapId");
                //above code is equivalent to following
                //var group = null;
                //if (Util.isArray(objectInfo.participants)) {
                //    for (var i = 0; i < objectInfo.participants.length; i++) {
                //        var participant = objectInfo.participants[i];
                //        if ("owning group" == participant.participantType) {
                //            group = participant.participantLdapId;
                //            break;
                //        }
                //    }
                //}
                //return group;
            }

            /**
             * @ngdoc method
             * @name setGroup
             * @methodOf services:Object.ModelService
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
                    var found = _.find(objectInfo.participants, {participantType: "owning group"});
                    if (found) {
                        found.participantLdapId = assignee;
                    } else {
                        objectInfo.participants.push({
                            participantType: "owning group"
                            , participantLdapId: group
                        });
                    }

                    //for (var i = 0; i < objectInfo.participants.length; i++) {
                    //    if ("owning group" == objectInfo.participants[i].participantType) {
                    //        objectInfo.participants[i].participantLdapId = group;
                    //        return;
                    //    }
                    //}
                    //
                    //var participant = {};
                    //participant.participantType = "owning group";
                    //participant.participantLdapId = group;
                    //objectInfo.participants.push(participant);
                }
            }
        }
    }
]);