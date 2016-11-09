'use strict';

/**
 * @ngdoc service
 * @name services:Object.ModelService
 *
 * @description
 *
 * {@link https://gitlab.armedia.com/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object-model.client.service.js services/object-model.client.service.js}

 * CallObjectsService contains wrapper functions of ObjectsService to support default error handling, data validation and data cache.
 */
angular.module('services').factory('Object.ModelService', ['$q', '$resource', 'UtilService', 'Admin.OrganizationalHierarchyService',
    function ($q, $resource, Util, AdminOrganizationalHierarchyService) {
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
            },
            /**
             * @ngdoc method
             * @name getParticipantByType
             * @methodOf services:Object.ModelService
             *
             * @description
             * Search for participant type from object data
             *
             * @param {Object} objectInfo  Object data. Can be CaseFile, Complaint, Task, CostSheet, TimeSheet, ...
             * @param {String} participantType The type of participant (assignee, approver, ...) we are searching
             *
             * @returns {String} Participant LDAP ID, or 'null' if not found
             */
            getParticipantByType: function (objectInfo, participantType) {
                return _.result(_.find(Util.goodMapValue(objectInfo, "participants", []), {participantType: participantType}), "participantLdapId");
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
                        found.participantLdapId = group;
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

            /**
             * @ngdoc method
             * @name checkRestriction
             * @methodOf services:Object.ModelService
             *
             * @description
             * Check if Case or Complaint is restricted
             *
             * @param {String} userId Current login user ID
             * @param {String} assignee Assignee
             * @param {String} group Group LDAP ID
             * @param {Array} assignees List of assignees
             * @param {Array} groups List of Group
             *
             * @returns {Boolean} Return True if it is determined that it is restricted
             */
            , checkRestriction: function (userId, assignee, group, assignees, groups) {
                var restricted = true;

                // We need only one true condition.
                // First check if the assignee is the logged user
                if (!Util.isEmpty(assignee) && Util.compare(assignee, userId)) {
                    restricted = false;
                } else {
                    // If the user is not assignee, check in the assignees (users that belong to the group)
                    // Skip this check if the group is empty
                    if (Util.isArray(assignees) && !Util.isEmpty(group)) {
                        for (var i = 0; i < assignees.length; i++) {
                            if (Util.compare(assignees[i].userId, userId)) {
                                restricted = false;
                                break;
                            }
                        }
                    }

                    // If the user in not assignee or is not in the users that belong to a group, check if it's supervisor of the group
                    // Skip this check if the group is empty
                    if (restricted) {
                        if (Util.isArray(groups) && !Util.isEmpty(group)) {
                            for (var i = 0; i < groups.length; i++) {
                                if (Util.compare(groups[i].object_id_s, group)) {
                                    if (!Util.isEmpty(groups[i].supervisor_id_s) && Util.compare(groups[i].supervisor_id_s, userId)) {
                                        restricted = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                return restricted;
            }

            , checkIfUserCanRestrict: function (userId, objectInfo) {
                var owningGroup = this.getParticipantByType(objectInfo, "owning group");
                var assignee = this.getAssignee(objectInfo);
                var supervisor = this.getParticipantByType(objectInfo, "supervisor");
                var owningGroupName = owningGroup.replace(/\./g, '_002E_');
                if (Util.compare(assignee, userId)) {
                    return $q.resolve(true);
                } else if (Util.compare(supervisor, userId)) {
                    return $q.resolve(true);
                } else {
                    var canRestrict = AdminOrganizationalHierarchyService.getUsersForGroup(owningGroupName).then(function (data) {
                            var owningGroupUsers = _.get(data, 'data.response.docs');
                            var userInGroup = _.find(owningGroupUsers, function (user) {
                                return user.object_id_s === userId;
                            });
                            return userInGroup !== undefined ? true : false;
                        }
                    );
                    return $q.resolve(canRestrict);
                }
            }
        }
    }])
;