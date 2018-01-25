'use strict';

/**
 * @ngdoc service
 * @name services:Object.ParticipantService
 *
 * @description
 *
 * {@link https://***REMOVED***/arkcase/ACM3/tree/develop/acm-standard-applications/acm-law-enforcement/src/main/webapp/resources/services/object/object-participant.client.service.js services/object/object-participant.client.service.js}

 * Object.ParticipantService includes group of REST calls related to participants.
 */
angular
        .module('services')
        .factory(
                'Object.ParticipantService',
                [
                        '$resource',
                        '$translate',
                        '$q',
                        'UtilService',
                        'MessageService',
                        'SearchService',
                        'Search.QueryBuilderService',
                        function($resource, $translate, $q, Util, MessageService, SearchService, SearchQueryBuilder) {
                            var Service = $resource('api/v1/service', {}, {

                                /**
                                 * @ngdoc method
                                 * @name get
                                 * @methodOf services:Object.ParticipantService
                                 *
                                 * @description
                                 * Query list of participants for an object.
                                 *
                                 * @param {Object} params Map of input parameter
                                 * @param {String} params.objectType  Object type
                                 * @param {String} params.objectId  Object ID
                                 * @param {Function} onSuccess (Optional)Callback function of success query
                                 * @param {Function} onError (Optional) Callback function when fail
                                 *
                                 * @returns {Object} Object returned by $resource
                                 */
                                get : {
                                    method : 'GET',
                                    url : 'api/v1/service/participant/:objectType/:objectId',
                                    isArray : true
                                },

                                /**
                                 * @ngdoc method
                                 * @name save
                                 * @methodOf services:Object.ParticipantService
                                 *
                                 * @description
                                 * Create a new participant.
                                 *
                                 * @param {Object} params Map of input parameter
                                 * @param {String} params.userId  User ID
                                 * @param {String} params.participantType  Participant Type
                                 * @param {String} params.objectType  Object type
                                 * @param {String} params.objectId  Object ID
                                 * @param {Function} onSuccess (Optional)Callback function of success query
                                 * @param {Function} onError (Optional) Callback function when fail
                                 *
                                 * @returns {Object} Object returned by $resource
                                 */
                                save : {
                                    method : 'PUT',
                                    url : 'api/v1/service/participant/:userId/:participantType/:objectType/:objectId',
                                    cache : false
                                },

                                /**
                                 * @ngdoc method
                                 * @name delete
                                 * @methodOf services:Object.ParticipantService
                                 *
                                 * @description
                                 * Delete participant.
                                 *
                                 * @param {Object} params Map of input parameter
                                 * @param {String} params.userId  User ID
                                 * @param {String} params.participantType  Participant Type
                                 * @param {String} params.objectType  Object type
                                 * @param {String} params.objectId  Object ID
                                 * @param {Function} onSuccess (Optional)Callback function of success query
                                 * @param {Function} onError (Optional) Callback function when fail
                                 *
                                 * @returns {Object} Object returned by $resource
                                 */
                                _delete : {
                                    method : 'DELETE',
                                    url : 'api/v1/service/participant/:userId/:participantType/:objectType/:objectId',
                                    cache : false
                                },

                                /**
                                 * @ngdoc method
                                 * @name changeRole
                                 * @methodOf services:Object.ParticipantService
                                 *
                                 * @description
                                 * Change role for participant.
                                 *
                                 * @param {Object} params Map of input parameter
                                 * @param {String} params.participantId  Participant ID
                                 * @param {String} params.participantType  Participant Type
                                 * @param {Function} onSuccess (Optional)Callback function of success query
                                 * @param {Function} onError (Optional) Callback function when fail
                                 *
                                 * @returns {Object} Object returned by $resource
                                 */
                                changeRole : {
                                    method : 'DELETE',
                                    url : 'api/v1/service/participant/:participantId/:participantType',
                                    cache : false
                                },

                                /**
                                 * @ngdoc method
                                 * @name checkPersonGroup
                                 * @methodOf services:Object.ParticipantService
                                 *
                                 * @description
                                 * Check if a participant is a member of a group.
                                 *
                                 * @param {Object} params Map of input parameter
                                 * @param {String} params.objectType  Object type
                                 * @param {String} params.objectId  Object ID
                                 * @param {String} params.participantId  Participant ID
                                 * @param {String} params.groupId  Group ID
                                 * @param {Function} onSuccess (Optional)Callback function of success query
                                 * @param {Function} onError (Optional) Callback function when fail
                                 *
                                 * @returns {Object} Object returned by $resource
                                 */
                                checkGroupForParticipant : {
                                    method : 'GET',
                                    url : 'api/v1/plugin/search/advancedSearch?q=object_type_s\\:USER+'
                                            + '+AND+object_id_s\\::participantId+AND+groups_id_ss\\::owningGroup',
                                    data : ''
                                }

                            });

                            /**
                             * @ngdoc method
                             * @name findParticipantById
                             * @methodOf services:Object.ParticipantService
                             *
                             * @description
                             * Query participant of an object by Id
                             *
                             * @param {String} participantId  Participant id
                             *
                             * @returns {Object} participant data
                             */
                            Service.findParticipantById = function(participantId) {
                                // determine exact object type so that the validation passes in object-participant.client.service.js
                                var df = $q.defer();
                                var query = SearchQueryBuilder.buildSafeFqFacetedSearchQuery('* AND (id:"' + participantId
                                        + '-USER" OR (name:"' + participantId + '" AND object_type_s:GROUP))', "", 10, 0);
                                SearchService.queryFilteredSearch({
                                    query : query
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
                             * @description
                             * Query list of participants of an object
                             *
                             * @param {String} objectType  Object type
                             * @param {Number} objectId  Object ID
                             *
                             * @returns {Object} Promise
                             */
                            Service.retrieveParticipants = function(objectType, objectId) {
                                return Util.serviceCall({
                                    service : Service.get,
                                    param : {
                                        objectType : objectType,
                                        objectId : objectId
                                    },
                                    onSuccess : function(data) {
                                        if (Service.validateParticipants(data)) {
                                            return data;
                                        }
                                    }
                                })
                            };

                            /**
                             * @ngdoc method
                             * @name addNewParticipant
                             * @methodOf services:Object.ParticipantService
                             *
                             * @description
                             * Query list of participants of an object
                             *
                             * @param {String} userId  User ID
                             * @param {String} participantType  Participant Type
                             * @param {String} objectType  Object type
                             * @param {Number} objectId  Object ID
                             *
                             * @returns {Object} Promise
                             */
                            Service.addNewParticipant = function(userId, participantType, objectType, objectId) {
                                return Util.serviceCall({
                                    service : Service.save,
                                    param : {
                                        userId : userId,
                                        participantType : participantType,
                                        objectType : objectType,
                                        objectId : objectId
                                    },
                                    data : {},
                                    onSuccess : function(data) {
                                        if (Service.validateParticipant(data)) {
                                            return data;
                                        }
                                    }
                                })
                            };

                            /**
                             * @ngdoc method
                             * @name removeParticipant
                             * @methodOf services:Object.ParticipantService
                             *
                             * @description
                             * Remove participant of an object.
                             *
                             * @param {String} userId  User ID
                             * @param {String} participantType  Participant Type
                             * @param {String} objectType  Object type
                             * @param {Number} objectId  Object ID
                             *
                             * @returns {Object} Promise
                             */
                            Service.removeParticipant = function(userId, participantType, objectType, objectId) {
                                return Util.serviceCall({
                                    service : Service._delete,
                                    param : {
                                        userId : userId,
                                        participantType : participantType,
                                        objectType : objectType,
                                        objectId : objectId
                                    },
                                    onSuccess : function(data) {
                                        if (Service.validateRemovedParticipant(data)) {
                                            return data;
                                        }
                                    }
                                })
                            };

                            /**
                             * @ngdoc method
                             * @name changeParticipantRole
                             * @methodOf services:Object.ParticipantService
                             *
                             * @description
                             * Change role for participant of an object.
                             *
                             * @param {String} participantId  Participant ID
                             * @param {String} participantType  Participant Type
                             *
                             * @returns {Object} Promise
                             */
                            Service.changeParticipantRole = function(participantId, participantType) {
                                return Util.serviceCall({
                                    service : Service.changeRole,
                                    param : {
                                        participantId : participantId,
                                        participantType : participantType
                                    },
                                    onSuccess : function(data) {
                                        if (Service.validateParticipant(data)) {
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
                             * @param {Object} data Participant object to be validated
                             * @returns {boolean} Promise
                             */
                            Service.isParticipantValid = function(data) {
                                if (Util.isArrayEmpty(data)) {
                                    //group/user is invalid (e.g. sync error/stale data)
                                    MessageService.error($translate
                                            .instant("common.directive.coreParticipants.message.error.userOrGroupNotFound"));
                                    return false;
                                }
                                if (Util.isArrayEmpty(data) && data.length > 1) {
                                    //can't have two participants with same id
                                    MessageService.error($translate
                                            .instant("common.directive.coreParticipants.message.error.duplicateUserOrGroup"));
                                    return false;
                                }
                                return true;
                            };

                            /**
                             * @ngdoc method
                             * @name validateType
                             * @methodOf services:Object.ParticipantService
                             * @description Check if the type of participant is consistent with the given USER or GROUP type
                             * @param {Object} data Participant object to be validated
                             * @param {Object} type Given type
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
                             * @description
                             * Validate participants.
                             *
                             * @param {Object} data  Data to be validated
                             *
                             * @returns {Object} Promise
                             */
                            Service.validateParticipants = function(data) {
                                if (Util.isEmpty(data)) {
                                    return false;
                                }
                                if (!Util.isArray(data)) {
                                    return false;
                                }
                                if (_.filter(data, function(pa) {
                                    return Util.compare("assignee", pa.participantType);
                                }).length > 1) {
                                    MessageService.error($translate
                                            .instant("common.directive.coreParticipants.message.error.assigneeUnique"));
                                    return false;
                                }
                                if (_.filter(data, function(pa) {
                                    return Util.compare("owner", pa.participantType);
                                }).length > 1) {
                                    MessageService.error($translate.instant("common.directive.coreParticipants.message.error.ownerUnique"));
                                    return false;
                                }
                                if (_.filter(data, function(pa) {
                                    return Util.compare("owning group", pa.participantType);
                                }).length > 1) {
                                    MessageService.error($translate
                                            .instant("common.directive.coreParticipants.message.error.owninggroupUnique"));
                                    return false;
                                }
                                return true;
                            };

                            /**
                             * @ngdoc method
                             * @name validateParticipant
                             * @methodOf services:Object.ParticipantService
                             *
                             * @description
                             * Validate participant.
                             *
                             * @param {Object} data  Data to be validated
                             *
                             * @returns {Object} Promise
                             */
                            Service.validateParticipant = function(data) {
                                if (Util.isEmpty(data)) {
                                    return false;
                                }
                                if (Util.isEmpty(data.id)) {
                                    return false;
                                }
                                if (Util.isEmpty(data.objectType)) {
                                    return false;
                                }
                                if (Util.isEmpty(data.objectId)) {
                                    return false;
                                }
                                if (Util.isEmpty(data.participantType)) {
                                    return false;
                                }
                                if (Util.isEmpty(data.participantLdapId)) {
                                    return false;
                                }
                                return true;
                            };

                            /**
                             * @ngdoc method
                             * @name validateRemovedParticipant
                             * @methodOf services:Object.ParticipantService
                             *
                             * @description
                             * Validate participant that will be removed.
                             *
                             * @param {Object} data  Data to be validated
                             *
                             * @returns {Object} Promise
                             */
                            Service.validateRemovedParticipant = function(data) {
                                if (Util.isEmpty(data)) {
                                    return false;
                                }
                                if (Util.isEmpty(data.deletedParticipant)) {
                                    return false;
                                }
                                if (Util.isEmpty(data.deletedParticipantId)) {
                                    return false;
                                }
                                return true;
                            };

                            /**
                             * @ngdoc method
                             * @name isParticipantMemberOfGroup
                             * @methodOf services:Object.ParticipantService
                             *
                             * @description
                             * Query if participant(owner/assignee) belongs to selected group
                             *
                             * @param {String} participantId  Participant id
                             *
                             * @returns {Object} participant data
                             */
                            Service.isParticipantMemberOfGroup = function(participantId, owningGroup) {
                                return Util.serviceCall({
                                    service : Service.checkGroupForParticipant,
                                    param : {
                                        participantId : participantId,
                                        owningGroup : owningGroup
                                    },
                                    onSuccess : function(data) {
                                        if (data.response) {
                                            return data.response.docs.length > 0 ? true : false;
                                        }
                                    }
                                })
                            };

                            return Service;
                        } ]);