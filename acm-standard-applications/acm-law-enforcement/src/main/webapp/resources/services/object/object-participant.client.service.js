'use strict';

/**
 * @ngdoc service
 * @name services:Object.ParticipantService
 * 
 * @description
 * 
 * {@link /acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-participant.client.service.js services/object/object-participant.client.service.js}
 * 
 * Object.ParticipantService includes group of REST calls related to participants.
 */
angular.module('services').factory('Object.ParticipantService',
        [ '$resource', '$translate', '$q', 'UtilService', 'MessageService', 'SearchService', 'Search.QueryBuilderService', 'ObjectService', function($resource, $translate, $q, Util, MessageService, SearchService, SearchQueryBuilder, ObjectService) {
            var Service = $resource('api/v1/service', {}, {

                /**
                 * @ngdoc method
                 * @name get
                 * @methodOf services:Object.ParticipantService
                 * 
                 * @description Query list of participants for an object.
                 * 
                 * @param {Object}
                 *                params Map of input parameter
                 * @param {String}
                 *                params.objectType Object type
                 * @param {String}
                 *                params.objectId Object ID
                 * @param {Function}
                 *                onSuccess (Optional)Callback function of success query
                 * @param {Function}
                 *                onError (Optional) Callback function when fail
                 * 
                 * @returns {Object} Object returned by $resource
                 */
                get: {
                    method: 'GET',
                    url: 'api/v1/service/participant/:objectType/:objectId',
                    isArray: true
                },

                postEcmObjectParticipants: {
                    method: 'POST',
                    url: 'api/latest/service/ecm/participants/:objectType/:objectId',
                    cache: false,
                    isArray: true
                },

                /**
                 * @ngdoc method
                 * @name checkPersonGroup
                 * @methodOf services:Object.ParticipantService
                 * 
                 * @description Check if a participant is a member of a group.
                 * 
                 * @param {Object}
                 *                params Map of input parameter
                 * @param {String}
                 *                params.objectType Object type
                 * @param {String}
                 *                params.objectId Object ID
                 * @param {String}
                 *                params.participantId Participant ID
                 * @param {String}
                 *                params.groupId Group ID
                 * @param {Function}
                 *                onSuccess (Optional)Callback function of success query
                 * @param {Function}
                 *                onError (Optional) Callback function when fail
                 * 
                 * @returns {Object} Object returned by $resource
                 */
                checkGroupForParticipant: {
                    method: 'GET',
                    url: 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:USER+' + '+AND+object_id_s\\::participantId+AND+groups_id_ss\\::owningGroup',
                    data: ''
                }

            });

            /**
             * @ngdoc method
             * @name findParticipantById
             * @methodOf services:Object.ParticipantService
             * 
             * @description Query participant of an object by Id
             * 
             * @param {String}
             *                participantId Participant id
             * 
             * @returns {Object} participant data
             */
            Service.findParticipantById = function(participantId) {
                // determine exact object type so that the validation passes in object-participant.client.service.js
                var df = $q.defer();
                var query = SearchQueryBuilder.buildSafeFqFacetedSearchQuery('* AND (id:"' + participantId + '-USER" OR (name:"' + participantId + '" AND object_type_s:GROUP))', "", 10, 0);
                SearchService.unescapedQueryFilteredSearch({
                    unescapedQuery: query
                }, function(data) {
                    if (Util.validateSolrData(data)) {
                        var participantData = data.response.docs;
                        if (Service.isParticipantValid(participantData)) {
                            return df.resolve(participantData);
                        }
                    }
                    return df.resolve([]);
                })
                return df.promise;
            };

            /**
             * @ngdoc method
             * @name retrieveParticipants
             * @methodOf services:Object.ParticipantService
             * 
             * @description Query list of participants of an object
             * 
             * @param {String}
             *                objectType Object type
             * @param {Number}
             *                objectId Object ID
             * 
             * @returns {Object} Promise
             */
            Service.retrieveParticipants = function(objectType, objectId) {
                return Util.serviceCall({
                    service: Service.get,
                    param: {
                        objectType: objectType,
                        objectId: objectId
                    },
                    onSuccess: function(data) {
                        if (Service.validateParticipants(data)) {
                            return data;
                        }
                    }
                })
            };

            /**
             * @ngdoc method
             * @name isParticipantValid
             * @methodOf services:Object.ParticipantService
             * @description Check if the participant is valid
             * @param {Object}
             *                data Participant object to be validated
             * @returns {boolean} Promise
             */
            Service.isParticipantValid = function(data) {
                if (Util.isArrayEmpty(data)) {
                    // group/user is invalid (e.g. sync error/stale data)
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.userOrGroupNotFound"));
                    return false;
                }
                if (Util.isArrayEmpty(data) && data.length > 1) {
                    // can't have two participants with same id
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.duplicateUserOrGroup"));
                    return false;
                }
                return true;
            };

            /**
             * @ngdoc method
             * @name validateType
             * @methodOf services:Object.ParticipantService
             * @description Check if the type of participant is consistent with the given USER or GROUP type
             * @param {Object}
             *                data Participant object to be validated
             * @param {Object}
             *                type Given type
             * @returns {boolean} Promise
             */
            Service.validateType = function(data, type) {
                if (data.participantType == "owning group" && type != "GROUP") {
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.groupType"));
                    return false;
                }
                return true;
            };

            /**
             * @ngdoc method
             * @name validateParticipants
             * @methodOf services:Object.ParticipantService
             * 
             * @description Validate participants.
             * 
             * @param {Object}
             *                participants Participants array to be validated
             * @param {Boolean}
             *                allowDuplicateLdapIds When true duplicate LdapIds aren't checked (for entities usually true,
             *                for files it is false)
             * 
             * @returns {Boolean} true if participants are valid, otherwise false
             */
            Service.validateParticipants = function(participants, allowDuplicateLdapIds) {
                if (Util.isEmpty(participants)) {
                    return false;
                }
                if (!Util.isArray(participants)) {
                    return false;
                }

                // missing participant Ldap id
                if (_.find(participants, function(participant) {
                    return !participant.participantLdapId && participant.participantType != "assignee";
                })) {
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.emptyParticipantLdapId"));
                    return false;
                }

                // missing participantType
                if (_.find(participants, function(participant) {
                    return !participant.participantType;
                })) {
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.emptyParticipantType"));
                    return false;
                }

                // multiple assignees
                if (_.filter(participants, function(pa) {
                    return Util.compare("assignee", pa.participantType);
                }).length > 1) {
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.assigneeUnique"));
                    return false;
                }

                // multiple owners
                if (_.filter(participants, function(pa) {
                    return Util.compare("owner", pa.participantType);
                }).length > 1) {
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.ownerUnique"));
                    return false;
                }

                // multiple owning groups
                if (_.filter(participants, function(pa) {
                    return Util.compare("owning group", pa.participantType);
                }).length > 1) {
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.owninggroupUnique"));
                    return false;
                }

                // don't understand this check. Is " " a valid participant type?
                if (_.filter(participants, function(pa) {
                    return Util.compare(" ", pa.participantType);
                }).length > 1) {
                    return false;
                }

                // check for duplicate roles for LdapId
                if (_.chain(participants).groupBy('participantLdapId').filter(function(array) {
                    return array.length > 1
                }).flatten().groupBy('participantType').filter(function(array) {
                    return array.length > 1
                }).flatten().value().length > 0) {
                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.duplicateUserOrGroup"));
                    return false;
                }

                if (!allowDuplicateLdapIds) {
                    // search for duplicate participants LDAPIds. One participant cannot have different roles for an object
                    if (_.chain(participants).groupBy('participantLdapId').filter(function(v) {
                        return v.length > 1
                    }).flatten().value().length > 0) {
                        MessageService.error($translate.instant("common.directive.coreParticipants.message.error.duplicateUserOrGroup"));
                        return false;
                    }
                }

                return true;
            };

            Service.getFileParticipantsAsObjectInfo = function(fileId) {
                return Service.getObjectParticipantsAsObjectInfo(ObjectService.ObjectTypes.FILE, fileId);
            };

            Service.getFolderParticipantsAsObjectInfo = function(folderId) {
                return Service.getObjectParticipantsAsObjectInfo(ObjectService.ObjectTypes.FOLDER, folderId);
            };

            Service.getObjectParticipantsAsObjectInfo = function(objectType, objectId) {
                return Service.retrieveParticipants(objectType, objectId).then(function(data) {
                    return {
                        "participants": data
                    };
                });
            };

            /**
             * @ngdoc method
             * @name validateObjectParticipants
             * @methodOf services:Object.ParticipantService
             * 
             * @description Validate object participants
             * 
             * @param {Object}
             *                data Object with 'participants' property to be validated
             * @param {Boolean}
             *                allowDuplicateLdapIds When true duplicate LdapIds aren't checked (for entities usually true, for files it is
             *                false)
             * 
             * @returns {Boolean} Return true if data is valid
             */
            Service.validateObjectParticipants = function(data, allowDuplicateLdapIds) {
                return Service.validateParticipants(data.participants, allowDuplicateLdapIds);
            };

            /**
             * @ngdoc method
             * @name saveFileParticipants
             * @methodOf services:Object.ParticipantService
             * 
             * @description Save file participants
             * 
             * @param {Object}
             *                data Object with 'participants' array property to be saved and 'objectId' property as the fileId
             */
            Service.saveFileParticipants = function(data) {
                return Service.saveEcmObjectParticipants(ObjectService.ObjectTypes.FILE, data);
            };

            /**
             * @ngdoc method
             * @name saveFolderParticipants
             * @methodOf services:Object.ParticipantService
             * 
             * @description Save folder participants
             * 
             * @param {Object}
             *                data Object with 'participants' array property to be saved and 'objectId' property as the folderId
             */
            Service.saveFolderParticipants = function(data) {
                return Service.saveEcmObjectParticipants(ObjectService.ObjectTypes.FOLDER, data);
            };

            /**
             * @ngdoc method
             * @name saveEcmObjectParticipants
             * @methodOf services:Object.ParticipantService
             * 
             * @description Save ecm file or folder participants
             * 
             * @param {Object}
             *                data Object with 'participants' array property to be saved and 'objectId' property as Id for the object
             * @param {String}
             *                objectType The object type to set the participants on
             */
            Service.saveEcmObjectParticipants = function(objectType, data) {
                if (Service.validateObjectParticipants(data, false)) {
                    return Util.serviceCall({
                        service: Service.postEcmObjectParticipants,
                        param: {
                            objectType: objectType,
                            objectId: data.objectId
                        },
                        data: data.participants,
                        onSuccess: function(data) {
                            return {
                                "participants": data
                            };
                        }
                    });
                }
            }

            /**
             * @ngdoc method
             * @name isParticipantMemberOfGroup
             * @methodOf services:Object.ParticipantService
             * 
             * @description Query if participant(owner/assignee) belongs to selected group
             * 
             * @param {String}
             *                participantId Participant id
             * 
             * @returns {Object} participant data
             */
            Service.isParticipantMemberOfGroup = function(participantId, owningGroup) {
                return Util.serviceCall({
                    service: Service.checkGroupForParticipant,
                    param: {
                        participantId: participantId,
                        owningGroup: owningGroup
                    },
                    onSuccess: function(data) {
                        if (data.response) {
                            return data.response.docs.length > 0 ? true : false;
                        }
                    }
                })
            };

            return Service;
        } ]);